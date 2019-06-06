package gl51.project.store

import io.micronaut.context.ApplicationContext
import io.micronaut.core.type.Argument
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class ProductControllerTest extends Specification {
    @Shared @AutoCleanup EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)
    @Shared @AutoCleanup RxHttpClient client = embeddedServer.applicationContext.createBean(RxHttpClient, embeddedServer.getURL())

	
    Product sampleProduct = new Product("parapluie ", 12)

    void "test empty index"() {
        given:
        List<Product> response = client.toBlocking().retrieve(HttpRequest.GET('/products'), Argument.listOf(Product).type)

        expect:
        response ==[]
    }

    void "test create"() {
        setup:
        Product newProduct = new Product(name: name, description: description, price: price, idealTemperature: idealTemperature)

        when:
        String id = client.toBlocking().retrieve(HttpRequest.POST('/products', newProduct))
        Product findProduct = client.toBlocking().retrieve(HttpRequest.GET('/products/'+id), Argument.of(Product).type)

        then:
        findProduct.name ==newProduct.name
        findProduct.description == newProduct.description
        findProduct.price == newProduct.price
        findProduct.idealTemperature == newProduct.idealTemperature

        where:
        name | description | price | idealTemperature
        "aaa" | "bbb" | 0.0 | 123000
    }

    void "test update"() {
   		setup:
      String id = client.toBlocking().retrieve(HttpRequest.POST("/products", testProduct))
      Product newProduct = new Product(name: "product2", description: "desc2", price: 0.1, idealTemperature: 0.1)

      when:
      client.toBlocking().retrieve(HttpRequest.PUT("/store/products/"+id, newProduct), Argument.of(HttpStatus).type)
      def productList = client.toBlocking().retrieve(HttpRequest.GET("/products"), Argument.listOf(Product).type)

      then:
      productList.last().name == newProduct.name
      productList.last().description == newProduct.description
      productList.last().price == newProduct.price
	productList.last().idealTemperature == newProduct.idealTemperature
    }

    void "test delete"() {
    setup:
    String id = client.toBlocking().retrieve(HttpRequest.POST("/products", testProduct))
    def productList = client.toBlocking().retrieve(HttpRequest.GET("/products"), Argument.listOf(Product).type)
    def size = productList.size()

    when:
    client.toBlocking().retrieve(HttpRequest.DELETE("/products/"+id), Argument.of(HttpStatus).type)
    productList = client.toBlocking().retrieve(HttpRequest.GET("/products"), Argument.listOf(Product).type)

    then:
	size == productList.size()+1
    }

}