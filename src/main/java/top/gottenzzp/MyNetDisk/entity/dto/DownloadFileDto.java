package top.gottenzzp.MyNetDisk.entity.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Author GottenZZP
 */

@Data
@Builder
public class DownloadFileDto {
    private String downloadCode;
    private String fileId;
    private String fileName;
    private String filePath;
}
