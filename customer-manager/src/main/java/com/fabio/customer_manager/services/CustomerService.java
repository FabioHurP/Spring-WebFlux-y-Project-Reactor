package com.fabio.customer_manager.services;

import com.fabio.customer_manager.enums.UpdateRoleOperation;
import com.fabio.customer_manager.tables.CustomerTable;
import com.fabio.customer_manager.tables.RoleTable;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CustomerService {

    Mono<CustomerTable> createCustomer(CustomerTable customerTable, Set<String> roleNames);

    Mono<Map<String, List<RoleTable>>> readRolesByEmail(String email);

    Mono<Void> deleteCustomer(Long id);

    Mono<CustomerTable> updateRoleInCustomer(Long id, Set<String> roleNames, UpdateRoleOperation operation);
}
