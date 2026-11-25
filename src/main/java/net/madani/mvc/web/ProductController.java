package net.madani.mvc.web;

import jakarta.validation.Valid;
import net.madani.mvc.entities.Product;
import net.madani.mvc.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/user/index")
    @PreAuthorize("hasRole('USER')")
    public String index(Model model,
                        @RequestParam(name = "keyword", defaultValue = "") String keyword){
        List<Product> products = productRepository.findByNameContains(keyword);
        model.addAttribute("productList", products);
        model.addAttribute("keyword", keyword);
        return "products";
    }

    @GetMapping("/")
    public String home(){
        return "redirect:/user/index";
    }

    @PostMapping("/admin/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@RequestParam(name = "id") Long id){
        productRepository.deleteById(id);
        return "redirect:/user/index";
    }

    @GetMapping("/admin/newProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public String newProduct(Model model){
        model.addAttribute("product", new Product());
        return "new-product";
    }

    @PostMapping("/admin/saveProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveProduct(@Valid Product product, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) return "new-product";
        boolean isUpdate = product.getId() != null;
        productRepository.save(product); // avec un id existant, save() fait une mise à jour
        return isUpdate ? "redirect:/user/index" : "redirect:/admin/newProduct";
    }

    @GetMapping("/admin/editProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public String editProduct(Model model, @RequestParam(name = "id") Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + id));
        model.addAttribute("product", product);
        return "new-product";
    }

    // RequestMapping (toutes méthodes) : un POST refusé est aussi redirigé ici
    @RequestMapping("/notAuthorized")
    public String notAuthorized(){
        return "notAuthorized";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
