package com.pungu.store.book_service.utilities;

import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.dtos.SliceResponse;
import com.pungu.store.book_service.entities.Book;
import org.springframework.data.domain.Slice;

import java.util.List;

public class SliceResponseUtil {
    public static SliceResponse<BookResponse> mapToSLiceResponse(List<BookResponse> content, Slice<Book> authorList){
        return new SliceResponse<>(
                content,
                authorList.getNumber(),
                authorList.getNumberOfElements(),
                authorList.getSize(),
                authorList.hasNext()
        );
    }
}
