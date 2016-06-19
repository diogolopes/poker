package br.lopes.poker;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import br.lopes.poker.helper.DDLValidator;

@Configuration
@EnableJpaRepositories(basePackages = "br.lopes.poker.repository")
public class H2TestConfig {

    @Bean
    public DataSource dataSource() {
        final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2).build();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        // vendorAdapter.setGenerateDdl(true);
        // vendorAdapter.setShowSql(true);

        final Map<String, String> jpaPropertyMap = new HashMap<>();
        jpaPropertyMap.put("hibernate.show_sql", "true");
        jpaPropertyMap.put("hibernate.hbm2ddl.auto", "create-drop");
        // jpaPropertyMap.put("hibernate.format_sql", "true");

        final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPersistenceUnitName("pokerPU");
        emf.setDataSource(dataSource());
        emf.setPackagesToScan("br.lopes.poker.domain");
        emf.setJpaVendorAdapter(vendorAdapter);
        emf.setJpaPropertyMap(jpaPropertyMap);
        emf.afterPropertiesSet();
        return emf.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory());
        return transactionManager;
    }

    @Bean
    public DDLValidator ddlValidator() {
        final DDLValidator ddlValidator = new DDLValidator();
        ddlValidator.setDataSource(dataSource());
        return ddlValidator;
    }

    /**
     * Manter comentado pq nao sabemos se o Jenkins tera essas portas
     * disponiveis. Utilize esses beans como ferramenta visual apenas durante o
     * desenvolvimento.
     * 
     * URL : http://localhost:8082 Connection : jdbc:h2:mem:testdb
     */

    @Bean(name = "h2WebServer",
            initMethod = "start",
            destroyMethod = "stop")
    public org.h2.tools.Server h2WebServer() throws SQLException {
        return org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
    }

    @Bean(initMethod = "start",
            destroyMethod = "stop")
    @DependsOn(value = "h2WebServer")
    public org.h2.tools.Server h2Server() throws SQLException {
        return org.h2.tools.Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

}