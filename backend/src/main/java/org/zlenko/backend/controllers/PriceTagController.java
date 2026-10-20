package org.zlenko.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.zlenko.backend.dto.PriceTagRequest;
import org.zlenko.backend.dto.PriceTagResponse;
import org.zlenko.backend.services.PriceTagService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pricetags")
public class PriceTagController {
    private final PriceTagService priceTagService;

    @GetMapping
    public Page<PriceTagResponse> getPriceTags(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return priceTagService.getPriceTags(search, page, size);
    }

    @PostMapping
    public PriceTagResponse createPriceTag(
            @Valid @RequestBody PriceTagRequest request) {

        return priceTagService.createNewPriceTag(request);
    }
}
