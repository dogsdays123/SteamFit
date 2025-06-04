package org.zerock.b01.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.service.MaterialService;
import org.zerock.b01.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductApiController {

    private final ProductService productService;
    private final MaterialService materialService;

    public ProductApiController(ProductService productService, MaterialService materialService) {
        this.productService = productService;
        this.materialService = materialService;
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/products/{PName}/materials")
    public List<Material> getMaterialsByProduct(@PathVariable String PName) {
        return materialService.getMaterialByPName(PName);
    }

}