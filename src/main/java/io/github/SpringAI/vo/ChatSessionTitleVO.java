/**
 * 类名:ChatSessionTitleVO
 * 创建人:lzw    创建时间:2026/10/12
 */

package io.github.SpringAI.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author lzw
 */
@Data
public class ChatSessionTitleVO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100,message = "标题不能超过100个字符")
    private String title;
}
