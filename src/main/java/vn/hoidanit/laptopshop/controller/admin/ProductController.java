package vn.hoidanit.laptopshop.controller.admin;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;

import java.util.List;

@Controller
public class ProductController {
    private final UploadService uploadService;
    private final ProductService productService;

    public ProductController(UploadService uploadService, ProductService productService) {
        this.uploadService = uploadService;
        this.productService = productService;
    }

    @GetMapping("/admin/product")
    public String getProductPage(Model model, @RequestParam(defaultValue = "1") int page) {
        Pageable pageable = PageRequest.of(page - 1, 2);
        Page<Product> prs = productService.fetchAllProducts(pageable);
        List<Product> listProducts = prs.getContent();
        model.addAttribute("products", listProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", prs.getTotalPages());
        return "admin/product/show";
    }

    @GetMapping("/admin/product/create")
    public String getCreatePage(Model model) {
        model.addAttribute("newProduct", new Product());
        return "admin/product/create";
    }

    @PostMapping("/admin/product/create")
    public String createProduct(@ModelAttribute("newProduct") @Valid Product product, BindingResult newProductBindingResult,
                                @RequestParam("myFile") MultipartFile file) {
        //validate
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/create";
        }
        //upload file
        String image = uploadService.handleSaveUploadFile(file, "product");
        product.setImage(image);

        productService.createProduct(product);
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/{id}")
    public String getDetailProduct(Model model, @PathVariable long id) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/product/detail";
    }

    @GetMapping("/admin/product/update/{id}")
    public String getUpdateProduct(Model model, @PathVariable long id) {
        Product currentProduct = productService.getProductById(id);
        model.addAttribute("product", currentProduct);
        return "admin/product/update";
    }

    @PostMapping("/admin/product/update")
    public String postUpdateProduct(@ModelAttribute("product") @Valid Product prd, BindingResult newProductBindingResult,
                                    @RequestParam("myFile") MultipartFile file) {
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/update";
        }

        Product currentProduct = productService.getProductById(prd.getId());
        if (currentProduct != null) {
            if (!file.isEmpty()) {
                String image = uploadService.handleSaveUploadFile(file, "product");
                currentProduct.setImage(image);
            }
            currentProduct.setName(prd.getName());
            currentProduct.setPrice(prd.getPrice());
            currentProduct.setDetailDesc(prd.getDetailDesc());
            currentProduct.setShortDesc(prd.getShortDesc());
            currentProduct.setQuantity(prd.getQuantity());
            currentProduct.setFactory(prd.getFactory());
            currentProduct.setTarget(prd.getTarget());
            productService.createProduct(currentProduct);
        }
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String getDeletePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("deleteProduct", productService.getProductById(id));
        return "admin/product/delete.jsp";
    }

    @PostMapping("/admin/product/delete")
    public String deleteProduct(@ModelAttribute("deleteProduct") Product product) {
        productService.deleteProductById(product.getId());
        return "redirect:/admin/product";
    }

}
