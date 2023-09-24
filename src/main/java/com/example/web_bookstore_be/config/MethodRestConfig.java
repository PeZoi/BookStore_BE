package com.example.web_bookstore_be.config;

import com.example.web_bookstore_be.entity.Genre;
import com.example.web_bookstore_be.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class MethodRestConfig implements RepositoryRestConfigurer {
    @Autowired
    private EntityManager entityManager;

    private String url = "http://localhost:8080";
     @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        HttpMethod[] disableMethods ={
                HttpMethod.POST,
                HttpMethod.PUT,
                HttpMethod.PATCH,
                HttpMethod.DELETE,
        };

        // Cấu hình thêm là cho phép trả về id của entity khi gọi endpoint get
        config.exposeIdsFor(entityManager.getMetamodel().getEntities().stream().map(Type::getJavaType).toArray(Class[]::new));

//        Chặn các phương thức của class entity cụ thể
//        disableHttpMethods(Genre.class, config, disableMethods);
//        disableHttpMethods(User.class, config, new HttpMethod[]{HttpMethod.DELETE});
    }

    private void disableHttpMethods(Class c, RepositoryRestConfiguration config, HttpMethod[] methods) {
        config.getExposureConfiguration()
                .forDomainType(c)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(methods))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(methods));
    }
}
