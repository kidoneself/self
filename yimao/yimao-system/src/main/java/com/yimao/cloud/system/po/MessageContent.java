package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.MessageContentDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "t_message_content")
public class MessageContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;

    public MessageContent() {
    }

    public MessageContent(MessageContentDTO dto) {
        this.id = dto.getId();
        this.content = dto.getContent();

    }

    public void convert(MessageContentDTO dto) {
        dto.setId(this.id);
        dto.setContent(this.content);
    }
}
