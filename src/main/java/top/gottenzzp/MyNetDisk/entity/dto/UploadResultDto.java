package top.gottenzzp.MyNetDisk.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 上传结果dto
 *
 * @Title: UploadResultDto
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.entity.dto
 * @Date 2024/1/16 12:22
 * @description: 上传文件后返回前端的内容
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadResultDto {
    private String fileId;
    private String status;
}
