package cn.iocoder.yudao.module.email.service;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 本地文件存储服务
 *
 * @author 方总牛逼
 */
@Service
@Slf4j
public class LocalFileStorageService {

    @Value("${yudao.email.local-storage.base-path:/data/email-attachments}")
    private String basePath;

    /**
     * 保存附件到本地文件系统
     *
     * @param content 文件内容
     * @param originalFilename 原始文件名
     * @return 相对文件路径
     */
    public String saveAttachment(byte[] content, String originalFilename) {
        try {
            // 生成文件存储路径: /年/月/日/UUID_原始文件名
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String extension = getFileExtension(originalFilename);
            String filename = uuid + "_" + cleanFilename(originalFilename);
            
            String relativePath = datePath + "/" + filename;
            Path fullPath = Paths.get(basePath, relativePath);
            
            // 创建目录
            Files.createDirectories(fullPath.getParent());
            
            // 保存文件
            Files.write(fullPath, content);
            
            log.info("附件保存成功: {}", relativePath);
            return relativePath;
            
        } catch (IOException e) {
            log.error("保存附件失败: {}", originalFilename, e);
            throw new ServiceException(500, "保存附件失败");
        }
    }

    /**
     * 读取附件内容
     *
     * @param relativePath 相对路径
     * @return 文件内容
     */
    public byte[] getAttachment(String relativePath) {
        try {
            Path fullPath = Paths.get(basePath, relativePath);
            if (!Files.exists(fullPath)) {
                throw new ServiceException(404, "附件不存在");
            }
            return Files.readAllBytes(fullPath);
        } catch (IOException e) {
            log.error("读取附件失败: {}", relativePath, e);
            throw new ServiceException(500, "读取附件失败");
        }
    }

    /**
     * 删除附件
     *
     * @param relativePath 相对路径
     */
    public void deleteAttachment(String relativePath) {
        try {
            Path fullPath = Paths.get(basePath, relativePath);
            Files.deleteIfExists(fullPath);
            log.info("附件删除成功: {}", relativePath);
        } catch (IOException e) {
            log.error("删除附件失败: {}", relativePath, e);
        }
    }

    /**
     * 获取附件完整路径
     *
     * @param relativePath 相对路径
     * @return 完整路径
     */
    public String getFullPath(String relativePath) {
        return Paths.get(basePath, relativePath).toString();
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex >= 0 ? filename.substring(lastDotIndex) : "";
    }

    /**
     * 清理文件名，移除不安全字符
     */
    private String cleanFilename(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "unnamed";
        }
        // 移除路径分隔符和其他不安全字符
        return filename.replaceAll("[/\\\\:*?\"<>|]", "_")
                      .substring(0, Math.min(filename.length(), 100)); // 限制长度
    }
} 