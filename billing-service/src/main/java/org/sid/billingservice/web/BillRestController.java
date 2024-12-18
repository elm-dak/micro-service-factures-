package org.sid.billingservice.web;

import org.sid.billingservice.entities.Bill;
import org.sid.billingservice.entities.ProductItem;
import org.sid.billingservice.feign.CustomerRestClient;
import org.sid.billingservice.feign.ProductRestClient;
import org.sid.billingservice.repositories.BillRepository;
import org.sid.billingservice.repositories.ProductItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BillRestController {

    private final BillRepository billRepository;
    private final ProductItemRepository productItemRepository;
    private final CustomerRestClient customerRestClient;
    private final ProductRestClient productRestClient;

    public BillRestController(BillRepository billRepository,
                              ProductItemRepository productItemRepository,
                              CustomerRestClient customerRestClient,
                              ProductRestClient productRestClient) {
        this.billRepository = billRepository;
        this.productItemRepository = productItemRepository;
        this.customerRestClient = customerRestClient;
        this.productRestClient = productRestClient;
    }

    @GetMapping(path = "/fullBills/{id}")
    public Bill getBill(@PathVariable(name = "id") Long id) {
        Bill bill = billRepository.findById(id).orElseThrow(() -> new RuntimeException("Bill not found"));
        bill.setCustomer(customerRestClient.findCustomerById(bill.getCustomerId()));
        bill.getProductItems().forEach(pi -> {
            pi.setProduct(productRestClient.findProductById(pi.getProductId()));
        });
        return bill;
    }

    // New endpoint to fetch product items for a specific bill
    @GetMapping(path = "/fullBills/{id}/productItems")
    public List<ProductItem> getProductItems(@PathVariable(name = "id") Long billId) {
        try {
            // Log the billId to confirm it's being passed correctly
            System.out.println("Fetching product items for bill ID: " + billId);
            // Get the bill from the database
            Bill bill = billRepository.findById(billId).orElseThrow(() -> new RuntimeException("Bill not found"));
            // Fetch product items associated with the bill
            List<ProductItem> productItems = productItemRepository.findByBillId(billId);
            // Log the fetched product items
            System.out.println("Fetched product items: " + productItems);
            return productItems;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during fetching product items: " + e.getMessage());
        }
    }

}
