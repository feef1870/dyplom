package org.zlenko.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.zlenko.backend.dto.PriceTagRequest;
import org.zlenko.backend.dto.PriceTagResponse;
import org.zlenko.backend.dto.UpdatePriceTagRequest;
import org.zlenko.backend.services.PriceTagService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/price-tags")
public class PriceTagController {
    private final PriceTagService priceTagService;

    @GetMapping
    public Page<PriceTagResponse> getPriceTags(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return priceTagService.getPriceTags(search, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PriceTagResponse createPriceTag(
            @Valid @RequestBody PriceTagRequest request
    ) {
        return priceTagService.createPriceTag(request);
    }

    @PatchMapping("/{id}")
    public PriceTagResponse updatePriceTag(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePriceTagRequest request
    ) {
        return priceTagService.updatePriceTag(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePriceTag(
            @PathVariable Long id
    ) {
        priceTagService.deletePriceTag(id);
    }
}
