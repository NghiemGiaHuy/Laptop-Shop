package vn.hoidanit.laptopshop.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.laptopshop.domain.*;
import vn.hoidanit.laptopshop.dto.ProductCriteriaDTO;
import vn.hoidanit.laptopshop.repository.*;
import vn.hoidanit.laptopshop.specification.ProductSpecs;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductSpecs productSpecs;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;

    public ProductService(ProductRepository productRepository, CartRepository cartRepository, CartDetailRepository cartDetailRepository, ProductSpecs productSpecs, UserRepository userRepository, OrderDetailRepository orderDetailRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.productSpecs = productSpecs;
        this.userRepository = userRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }


    public Page<Product> findAll(Pageable pageable, ProductCriteriaDTO productCriteriaDTO) {
        Specification<Product> combineSpecifications = Specification.where(null);
        if ((productCriteriaDTO.getFactory() != null) && (productCriteriaDTO.getFactory().isPresent())) {
            combineSpecifications = combineSpecifications.and(productSpecs.matchList(productCriteriaDTO.getFactory().get(), Product_.FACTORY));
        }
        if ((productCriteriaDTO.getTarget() != null) && (productCriteriaDTO.getTarget().isPresent())) {
            combineSpecifications = combineSpecifications.and(productSpecs.matchList(productCriteriaDTO.getTarget().get(), Product_.TARGET));
        }
        if ((productCriteriaDTO.getPrice() != null) && (productCriteriaDTO.getPrice().isPresent())) {
            combineSpecifications = combineSpecifications.and(buildPriceSpecification(productCriteriaDTO.getPrice().get()));
        }
        return productRepository.findAll(combineSpecifications, pageable);
    }

    private Specification<Product> buildPriceSpecification(List<String> prices) {
        Specification<Product> combineSpecifications = Specification.where(null);
        for (String p : prices) {
            double min = 0;
            double max = 0;
            switch (p) {
                case "duoi-10-trieu":
                    min = Double.MIN_VALUE;
                    max = 10_000_000;
                    break;
                case "tu-10-15-trieu":
                    min = 10_000_000;
                    max = 15_000_000;
                    break;
                case "tu-15-20-trieu":
                    min = 15_000_000;
                    max = 20_000_000;
                    break;
                case "tren-20-trieu":
                    min = 20_000_000;
                    max = Double.MAX_VALUE;
                    break;
                default:
                    break;
            }
            if (min != 0 && max != 0) {
                combineSpecifications = combineSpecifications.or(productSpecs.matchPrice(min, max));
            }
        }
        return combineSpecifications;
    }

    public PageRequest getPageRequest(ProductCriteriaDTO productCriteriaDTO, int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 6);
        if ((productCriteriaDTO.getSort() != null) && (productCriteriaDTO.getSort().isPresent())) {
            String sort = productCriteriaDTO.getSort().get();
            if (sort.equals("gia-tang-dan")) {
                pageRequest = PageRequest.of(page - 1, 6, Sort.by(Product_.PRICE).ascending());
            } else if (sort.equals("gia-giam-dan")) {
                pageRequest = PageRequest.of(page - 1, 6, Sort.by(Product_.PRICE).descending());
            }
        }
        return pageRequest;
    }

    public Page<Product> fetchAllProducts(Pageable page) {
        return productRepository.findAll(page);
    }

    public Product getProductById(long id) {
        return productRepository.findById(id);
    }

    public void deleteProductById(long id) {
        productRepository.deleteById(id);
    }

    public void handleAddProductToCart(HttpSession session, long id, long quantity) {
        User user = userRepository.findByEmail(session.getAttribute("email").toString());
        if (user != null) {
            Cart cart = cartRepository.findByUser(user);
            if (cart == null) {
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setSum(0);

                cart = cartRepository.save(newCart);
            }

            Product product = productRepository.findById(id);
            if (product != null) {
                CartDetail existCartDetail = cartDetailRepository.findByCartAndProduct(cart, product);
                if (existCartDetail == null) {
                    CartDetail cartDetail = new CartDetail();
                    cartDetail.setCart(cart);
                    cartDetail.setProduct(product);
                    cartDetail.setPrice(product.getPrice());
                    cartDetail.setQuantity(quantity);
                    cartDetailRepository.save(cartDetail);

                    int s = cart.getSum() + 1;
                    cart.setSum(s);
                    cartRepository.save(cart);
                    session.setAttribute("sum", s);
                } else {
                    existCartDetail.setQuantity(existCartDetail.getQuantity() + quantity);
                    cartDetailRepository.save(existCartDetail);
                }
            }
        }
    }

    public Cart fetchByUser(User user) {
        return cartRepository.findByUser(user);
    }

    public void handleRemoveCartDetail(long cartDetailId, HttpSession session) {
        Optional<CartDetail> cartDetailOptional = cartDetailRepository.findById(cartDetailId);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            Cart currentCart = cartDetail.getCart();

            //delete.jsp cart-detail
            cartDetailRepository.deleteById(cartDetailId);

            //update cart
            if (currentCart.getSum() > 1) {
                //update current cart
                int s = currentCart.getSum() - 1;
                currentCart.setSum(s);
                session.setAttribute("sum", s);
                cartRepository.save(currentCart);
            } else {
                cartRepository.deleteById(currentCart.getId());
                session.setAttribute("sum", 0);

            }
        }
    }

    public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails) {
        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> cdOptional = cartDetailRepository.findById(cartDetail.getId());
            if (cdOptional.isPresent()) {
                CartDetail currentCartDetail = cdOptional.get();
                currentCartDetail.setQuantity(cartDetail.getQuantity());
                cartDetailRepository.save(currentCartDetail);
            }
        }
    }

    public void handlePlaceOrder(User user, HttpSession session, String receiverName, String receiverAddress, String receiverPhone) {
        Cart cart = cartRepository.findByUser(user);
        if (cart != null) {
            List<CartDetail> cartDetails = cart.getCartDetails();
            if (cartDetails != null) {
                //create order
                Order order = new Order();
                order.setUser(user);
                order.setReceiverName(receiverName);
                order.setReceiverAddress(receiverAddress);
                order.setReceiverPhone(receiverPhone);
                order.setStatus("PENDING");
                double totalPrice = 0;
                for (CartDetail cd : cartDetails) {
                    totalPrice += cd.getPrice();
                }
                order.setTotalPrice(totalPrice);
                order = orderRepository.save(order);

                //create orderDetail
                for (CartDetail cd : cartDetails) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(cd.getProduct());
                    orderDetail.setPrice(cd.getPrice());
                    orderDetail.setQuantity(cd.getQuantity());
                    orderDetailRepository.save(orderDetail);
                }

                //step 2: delete.jsp cart_detail và cart
                for (CartDetail cd : cartDetails) {
                    cartDetailRepository.deleteById(cd.getId());
                }
                cartRepository.deleteById(cart.getId());

                //step 3: update session
                session.setAttribute("sum", 0);
            }
        }
    }
}




