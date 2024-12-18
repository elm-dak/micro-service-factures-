package org.sid.billingservice;

import org.sid.billingservice.Model.Customer;
import org.sid.billingservice.Model.Product;
import org.sid.billingservice.entities.Bill;
import org.sid.billingservice.entities.ProductItem;
import org.sid.billingservice.feign.CustomerRestClient;
import org.sid.billingservice.feign.ProductRestClient;
import org.sid.billingservice.repositories.BillRepository;
import org.sid.billingservice.repositories.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BillRepository billRepository,
                                        ProductItemRepository productItemRepository,
                                        CustomerRestClient customerRestClient,
                                        ProductRestClient productRestClient) {

        return args -> {
            try {
                // Step 1: Fetch all customers from CustomerService
                Collection<Customer> customers = customerRestClient.getAllCustomers().getContent();

                if (customers == null || customers.isEmpty()) {
                    throw new RuntimeException("No customers available");
                }

                // Step 2: Fetch products from InventoryService
                Collection<Product> products = productRestClient.allProducts().getContent();
                if (products == null || products.isEmpty()) {
                    throw new RuntimeException("No products available");
                }

                // Step 3: Create a new bill for each customer
                customers.forEach(customer -> {
                    try {
                        // Create a new bill for the customer
                        Bill bill = new Bill();
                        bill.setBillingDate(new Date());
                        bill.setCustomerId(customer.getId());  // Assign the customer to the bill
                        Bill savedBill = billRepository.save(bill); // Save the bill

                        // Step 4: Create ProductItems for each product
                        products.forEach(product -> {
                            ProductItem productItem = new ProductItem();
                            productItem.setBill(savedBill);
                            productItem.setProductId(product.getId());
                            productItem.setQuantity(1 + new Random().nextInt(10));  // Random quantity between 1 and 10
                            productItem.setPrice(product.getPrice());  // Price of the product
                            productItemRepository.save(productItem);  // Save the product item
                        });

                        System.out.println("Bill created for Customer ID: " + customer.getId());

                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Error creating bill for customer ID: " + customer.getId() + ": " + e.getMessage());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error during billing process: " + e.getMessage());
            }
        };
    }
}
