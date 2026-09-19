package com.unb.digitalbanking.controller;

import com.unb.digitalbanking.dto.TransferRequest;
import com.unb.digitalbanking.dto.TransferResponse;
import com.unb.digitalbanking.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse transfer(
            @Valid @RequestBody TransferRequest request
    ) {
        return transferService.transfer(request);
    }
}