package cn.iocoder.yudao.module.email.service;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.email.controller.admin.vo.ImportBatchPageReqVO;
import cn.iocoder.yudao.module.email.controller.admin.vo.ImportBatchRespVO;
import cn.iocoder.yudao.module.email.dal.dataobject.EmailMessageDO;
import cn.iocoder.yudao.module.email.dal.dataobject.EmailAttachmentDO;
import cn.iocoder.yudao.module.email.dal.dataobject.ImportBatchDO;
import cn.iocoder.yudao.module.email.dal.dataobject.ImportErrorLogDO;
import cn.iocoder.yudao.module.email.dal.mysql.EmailMessageMapper;
import cn.iocoder.yudao.module.email.dal.mysql.EmailAttachmentMapper;
import cn.iocoder.yudao.module.email.dal.mysql.ImportBatchMapper;
import cn.iocoder.yudao.module.email.dal.mysql.ImportErrorLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 邮件导入 Service 实现类
 *
 * @author 方总牛逼
 */
@Service
@Slf4j
public class EmailImportServiceImpl implements EmailImportService {

    @Resource
    private ImportBatchMapper importBatchMapper;

    @Resource
    private EmailMessageMapper emailMessageMapper;
    
    @Resource
    private EmailAttachmentMapper emailAttachmentMapper;
    
    @Resource
    private ImportErrorLogMapper importErrorLogMapper;

    @Resource
    private LocalFileStorageService localFileStorageService;

    @Override
    @Transactional
    public ImportBatchRespVO uploadZipFile(MultipartFile file) {
        log.info("开始处理ZIP文件上传: {}", file.getOriginalFilename());
        
        validateZipFile(file);

        ImportBatchDO batch = createImportBatch(file.getOriginalFilename());

        processZipFileAsync(file, batch.getId());

        return ImportBatchRespVO.builder()
                .id(batch.getId())
                .batchName(batch.getBatchName())
                .zipFilename(batch.getZipFilename())
                .status(batch.getStatus())
                .createTime(batch.getCreateTime())
                .build();
    }

    @Override
    public PageResult<ImportBatchDO> getImportBatchPage(ImportBatchPageReqVO pageReqVO) {
        return importBatchMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ImportErrorLogDO> getImportErrorLogs(Long batchId) {
        return importErrorLogMapper.selectListByBatchId(batchId);
    }

    @Override
    public ImportBatchRespVO getImportStatus(Long batchId) {
        ImportBatchDO batch = importBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new ServiceException(404, "导入批次不存在");
        }
        
        return ImportBatchRespVO.builder()
                .id(batch.getId())
                .batchName(batch.getBatchName())
                .zipFilename(batch.getZipFilename())
                .status(batch.getStatus())
                .totalFiles(batch.getTotalFiles())
                .successCount(batch.getSuccessCount())
                .failCount(batch.getFailCount())
                .startTime(batch.getStartTime())
                .endTime(batch.getEndTime())
                .errorMessage(batch.getErrorMessage())
                .createTime(batch.getCreateTime())
                .build();
    }

    private void validateZipFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException(400, "上传文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".zip")) {
            throw new ServiceException(400, "只支持ZIP格式的压缩文件");
        }
        if (file.getSize() > 100 * 1024 * 1024) { // 100MB
            throw new ServiceException(400, "文件大小不能超过100MB");
        }
    }

    private ImportBatchDO createImportBatch(String zipFilename) {
        ImportBatchDO batch = new ImportBatchDO();
        batch.setBatchName("邮件导入_" + LocalDateTime.now());
        batch.setZipFilename(zipFilename);
        batch.setStatus(ImportBatchDO.Status.PROCESSING.getCode());
        batch.setStartTime(LocalDateTime.now());
        importBatchMapper.insert(batch);
        return batch;
    }

    @Async("emailTaskExecutor")
    public void processZipFileAsync(MultipartFile file, Long batchId) {
        try {
            processZipFile(file, batchId);
        } catch (Exception e) {
            log.error("[processZipFileAsync][batchId({})] 异步处理ZIP文件失败", batchId, e);
            updateBatchStatus(batchId, ImportBatchDO.Status.FAILED, e.getMessage());
        }
    }

    public void processZipFile(MultipartFile file, Long batchId) {
        log.info("[processZipFile][batchId({})] 开始异步处理ZIP文件: {}", batchId, file.getOriginalFilename());
        int totalFiles = 0;
        int successCount = 0;

        // 使用Apache Commons Compress处理中文文件名
        try (ZipArchiveInputStream zis = new ZipArchiveInputStream(
                new BufferedInputStream(file.getInputStream()), "UTF-8", true, true)) {
            
            ZipArchiveEntry entry;
            while ((entry = zis.getNextZipEntry()) != null) {
                String entryName = entry.getName();
                log.debug("[processZipFile][batchId({})] 处理条目: {}", batchId, entryName);
                
                if (entry.isDirectory() || !entryName.toLowerCase().endsWith(".eml")) {
                    log.debug("[processZipFile][batchId({})] 跳过非EML文件: {}", batchId, entryName);
                    continue;
                }
                
                totalFiles++;
                try {
                    byte[] emlContent = readZipEntryContent(zis);
                    EmailMessageDO emailMessage = parseEmlContent(emlContent, entryName, batchId);
                    emailMessageMapper.insert(emailMessage);
                    
                    // 解析并保存附件
                    int attachmentCount = parseAndSaveAttachments(emlContent, emailMessage.getId());
                    
                    // 更新邮件的附件数量
                    if (attachmentCount > 0) {
                        EmailMessageDO updateObj = new EmailMessageDO();
                        updateObj.setId(emailMessage.getId());
                        updateObj.setAttachmentCount(attachmentCount);
                        emailMessageMapper.updateById(updateObj);
                    }
                    
                    successCount++;
                    log.info("[processZipFile][batchId({})] 成功解析EML文件: {} (附件数: {})", batchId, entryName, attachmentCount);
                } catch (Exception e) {
                    log.error("[processZipFile][batchId({})] 解析EML文件失败: {}", batchId, entryName, e);
                    saveErrorLog(batchId, entryName, "EML_PARSE_ERROR", e.getMessage());
                }
            }

            ImportBatchDO.Status finalStatus;
            if (totalFiles == 0) {
                finalStatus = ImportBatchDO.Status.SUCCESS; // 没有EML文件也算处理成功
            } else if (totalFiles == successCount) {
                finalStatus = ImportBatchDO.Status.SUCCESS;
            } else if (successCount > 0) {
                finalStatus = ImportBatchDO.Status.PARTIAL_FAILED;
            } else {
                finalStatus = ImportBatchDO.Status.FAILED;
            }
            
            updateBatchResult(batchId, finalStatus, totalFiles, successCount);

            log.info("[processZipFile][batchId({})] ZIP文件处理完成, 总计: {}, 成功: {}", batchId, totalFiles, successCount);

        } catch (IOException e) {
            log.error("[processZipFile][batchId({})] 处理ZIP文件IO异常: {}", batchId, file.getOriginalFilename(), e);
            updateBatchStatus(batchId, ImportBatchDO.Status.FAILED, "ZIP文件解析失败: " + e.getMessage());
        }
    }

    private byte[] readZipEntryContent(ZipArchiveInputStream zis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = zis.read(buffer)) > 0) {
            baos.write(buffer, 0, len);
        }
        return baos.toByteArray();
    }

    private EmailMessageDO parseEmlContent(byte[] emlContent, String fileName, Long batchId) throws MessagingException, IOException {
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(emlContent));

        EmailMessageDO emailMessage = new EmailMessageDO();
        emailMessage.setImportBatchId(batchId);
        emailMessage.setOriginalPath(fileName);
        emailMessage.setMessageId(message.getMessageID());
        emailMessage.setSubject(message.getSubject());

        if (message.getFrom() != null && message.getFrom().length > 0) {
            emailMessage.setSender(Arrays.stream(message.getFrom()).map(Address::toString).collect(Collectors.joining(", ")));
        }

        parseRecipients(message, emailMessage);

        if (message.getSentDate() != null) {
            emailMessage.setSendDate(message.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (message.getReceivedDate() != null) {
            emailMessage.setReceiveDate(message.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }

        // 解析邮件正文
        parseEmailBody(message, emailMessage);

        // 如果HTML内容存在但纯文本内容不存在，可以考虑从HTML生成纯文本摘要（此处暂不实现）
        if (emailMessage.getContentText() == null) {
            emailMessage.setContentText("");
        }
        if (emailMessage.getContentHtml() == null) {
            emailMessage.setContentHtml("");
        }
        
        // 附件数量稍后设置
        emailMessage.setAttachmentCount(0);
        emailMessage.setIsStarred(false);

        return emailMessage;
    }

    /**
     * 解析并保存邮件附件
     * @param emlContent EML文件内容
     * @param emailMessageId 邮件ID
     * @return 附件数量
     */
    private int parseAndSaveAttachments(byte[] emlContent, Long emailMessageId) throws MessagingException, IOException {
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(emlContent));
        
        AtomicInteger attachmentCount = new AtomicInteger(0);
        parseAttachmentsFromPart(message, emailMessageId, attachmentCount);
        
        return attachmentCount.get();
    }

    /**
     * 递归解析邮件附件
     * @param part 邮件部分
     * @param emailMessageId 邮件ID
     * @param attachmentCount 附件计数器
     */
    private void parseAttachmentsFromPart(Part part, Long emailMessageId, AtomicInteger attachmentCount) throws MessagingException, IOException {
        String disposition = part.getDisposition();
        
        // 检查是否为附件
        if (Part.ATTACHMENT.equalsIgnoreCase(disposition) || Part.INLINE.equalsIgnoreCase(disposition) || 
            (disposition == null && part.getFileName() != null)) {
            
            try {
                saveAttachment(part, emailMessageId);
                attachmentCount.incrementAndGet();
                log.debug("成功保存附件: {}", part.getFileName());
            } catch (Exception e) {
                log.warn("保存附件失败: {}", part.getFileName(), e);
            }
            return;
        }

        // 递归处理multipart内容
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int count = multipart.getCount();
            for (int i = 0; i < count; i++) {
                parseAttachmentsFromPart(multipart.getBodyPart(i), emailMessageId, attachmentCount);
            }
        } else if (part.isMimeType("message/rfc822")) {
            // 邮件中的邮件（转发）
            parseAttachmentsFromPart((Part) part.getContent(), emailMessageId, attachmentCount);
        }
    }

    /**
     * 保存单个附件
     * @param part 附件部分
     * @param emailMessageId 邮件ID
     */
    private void saveAttachment(Part part, Long emailMessageId) throws MessagingException, IOException {
        String fileName = part.getFileName();
        if (fileName == null) {
            fileName = "unnamed_attachment";
        }
        
        // 读取附件内容
        byte[] content;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            part.getDataHandler().writeTo(baos);
            content = baos.toByteArray();
        }
        
        // 保存到本地文件系统
        String filePath = localFileStorageService.saveAttachment(content, fileName);
        
        // 保存附件记录到数据库
        EmailAttachmentDO attachment = new EmailAttachmentDO();
        attachment.setEmailMessageId(emailMessageId);
        attachment.setFilename(fileName);
        attachment.setContentType(part.getContentType());
        attachment.setFileSize((long) content.length);
        attachment.setFilePath(filePath);
        attachment.setFileUrl(filePath); // 这里简化处理，实际可能需要构建完整URL
        
        emailAttachmentMapper.insert(attachment);
    }

    /**
     * 递归解析邮件正文 (处理 multipart 内容)
     * @param part 邮件部分
     * @param emailMessage 邮件数据对象
     */
    private void parseEmailBody(Part part, EmailMessageDO emailMessage) throws MessagingException, IOException {
        String disposition = part.getDisposition();
        
        // 跳过附件，只处理邮件正文
        if (Part.ATTACHMENT.equalsIgnoreCase(disposition) || Part.INLINE.equalsIgnoreCase(disposition) ||
            (disposition == null && part.getFileName() != null)) {
            return;
        }

        if (part.isMimeType("text/plain") && (emailMessage.getContentText() == null || emailMessage.getContentText().isEmpty())) {
            emailMessage.setContentText((String) part.getContent());
        } else if (part.isMimeType("text/html")) {
            emailMessage.setContentHtml((String) part.getContent());
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int count = multipart.getCount();
            for (int i = 0; i < count; i++) {
                // 递归解析每个部分
                parseEmailBody(multipart.getBodyPart(i), emailMessage);
            }
        } else if (part.isMimeType("message/rfc822")) {
            // 邮件中的邮件（转发）
            parseEmailBody((Part) part.getContent(), emailMessage);
        }
    }

    private void parseRecipients(MimeMessage message, EmailMessageDO emailMessage) throws MessagingException {
        if (message.getRecipients(Message.RecipientType.TO) != null) {
            emailMessage.setRecipients(JsonUtils.toJsonString(
                    Arrays.stream(message.getRecipients(Message.RecipientType.TO))
                            .map(addr -> ((InternetAddress) addr).getAddress())
                            .collect(Collectors.toList())));
        }
        if (message.getRecipients(Message.RecipientType.CC) != null) {
            emailMessage.setCcRecipients(JsonUtils.toJsonString(
                    Arrays.stream(message.getRecipients(Message.RecipientType.CC))
                            .map(addr -> ((InternetAddress) addr).getAddress())
                            .collect(Collectors.toList())));
        }
        if (message.getRecipients(Message.RecipientType.BCC) != null) {
            emailMessage.setBccRecipients(JsonUtils.toJsonString(
                    Arrays.stream(message.getRecipients(Message.RecipientType.BCC))
                            .map(addr -> ((InternetAddress) addr).getAddress())
                            .collect(Collectors.toList())));
        }
    }

    private void saveErrorLog(Long batchId, String filePath, String errorType, String errorMessage) {
        ImportErrorLogDO errorLog = new ImportErrorLogDO();
        errorLog.setImportBatchId(batchId);
        errorLog.setFilePath(filePath);
        errorLog.setErrorType(errorType);
        errorLog.setErrorMessage(errorMessage);
        importErrorLogMapper.insert(errorLog);
    }

    private void updateBatchStatus(Long batchId, ImportBatchDO.Status status, String errorMessage) {
        ImportBatchDO updateBatch = new ImportBatchDO();
        updateBatch.setId(batchId);
        updateBatch.setStatus(status.getCode());
        updateBatch.setEndTime(LocalDateTime.now());
        updateBatch.setErrorMessage(errorMessage);
        importBatchMapper.updateById(updateBatch);
    }

    private void updateBatchResult(Long batchId, ImportBatchDO.Status status, int totalFiles, int successCount) {
        ImportBatchDO updateBatch = new ImportBatchDO();
        updateBatch.setId(batchId);
        updateBatch.setStatus(status.getCode());
        updateBatch.setTotalFiles(totalFiles);
        updateBatch.setSuccessCount(successCount);
        updateBatch.setFailCount(totalFiles - successCount);
        updateBatch.setEndTime(LocalDateTime.now());
        importBatchMapper.updateById(updateBatch);
    }
}