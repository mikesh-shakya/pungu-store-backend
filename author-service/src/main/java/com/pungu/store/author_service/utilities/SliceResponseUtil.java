package com.pungu.store.author_service.utilities;

import com.pungu.store.author_service.dto.AuthorResponse;
import com.pungu.store.author_service.dto.SliceResponse;
import com.pungu.store.author_service.model.Author;
import org.springframework.data.domain.Slice;

import java.util.List;

public class SliceResponseUtil {
    public static SliceResponse<AuthorResponse> mapToSLiceResponse(List<AuthorResponse> content, Slice<Author> authorList){
        return new SliceResponse<>(
                content,
                authorList.getNumber(),
                authorList.getNumberOfElements(),
                authorList.getSize(),
                authorList.hasNext()
        );
    }
}
