package thjug.springboot.service.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import thjug.springboot.entity.Customer;
import thjug.springboot.service.CustomerService;

@Component
public class CustomerJdbcService implements CustomerService {

    private static final String QUERY_BY_FIRSTNAMENAME
            = "SELECT id, first_name, last_name FROM customers WHERE first_name = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Customer> queryByFirstname(String firstName) {
        return jdbcTemplate.query(
                QUERY_BY_FIRSTNAMENAME, new Object[]{"Josh"}, new CustomerRowMapper());
    }

    /**
     * (rs, rowNum) -> new Customer(rs.getLong("id"),
     *
     */
    class CustomerRowMapper implements RowMapper<Customer> {

        @Override
        public Customer mapRow(final ResultSet rs, final int i) throws SQLException {
            return new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"));
        }
    }

    class CustomerExtractor implements ResultSetExtractor<List<Customer>> {

        @Override
        public List<Customer> extractData(final ResultSet rs) throws SQLException, DataAccessException {
            final List<Customer> list = new LinkedList<>();

            while (rs.next()) {
                list.add(new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")));
            }

            return list;
        }
    }

}
