package com.study.rpe;

import com.study.rpe.model.Customer;
import com.study.rpe.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @PostMapping("/customer/create")
    public Mono<Customer> createCustomer(@RequestBody Customer customer) {

        return reactiveMongoTemplate.save(customer);
    }

    @GetMapping("/customer/{id}")
    public Mono<Customer> getCustomer(@PathVariable("id") String id) {

        return findCustomerById(id);
    }

    @PostMapping("/order/create")
    public Mono<Order> createOrder(@RequestBody Order order) {

        return reactiveMongoTemplate.save(order);
    }

    @GetMapping("/sales/summary")
    public Mono<Map<String, Double>> getSalesSummary() {

        return reactiveMongoTemplate.findAll(Customer.class)
                .flatMap(customer -> Mono.zip(Mono.just(customer), calculateOrderSum(customer.getId())))
                .collectMap(tuple2 -> tuple2.getT1().getName(), Tuple2::getT2);
    }

    private Mono<Customer> findCustomerById(String id) {

        Criteria criteria = Criteria.where("id").is(id);
        Query query = Query.query(criteria);

        return reactiveMongoTemplate.findOne(query, Customer.class);
    }

    private Mono<Double> calculateOrderSum(String customerId) {

        Criteria criteria = Criteria.where("customerId").is(customerId);
        Query query = Query.query(criteria);

        return reactiveMongoTemplate.find(query, Order.class)
                .map(Order::getTotal)
                .reduce(0d, Double::sum);
    }
}
