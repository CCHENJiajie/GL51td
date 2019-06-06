package gl51.project.store

import io.micronaut.context.ApplicationContext
import io.micronaut.core.type.Argument
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
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
        String id = client.toBlocking().retrieve(HttpRequest.POST('/store/product', sampleProduct))

        when:
        Product otherProduct = new Product( "parapluie", 15)
        HttpStatus status = client.toBlocking().retrieve(HttpRequest.PATCH('/store/product/' + id, otherProduct), Argument.of(HttpStatus).type)
        Product updatedProduct = client.toBlocking().retrieve(HttpRequest.GET('/store/product/' + id), Argument.of(Product).type)

        then:
        status == OK
        updatedProduct.getPrice() == otherProduct.getPrice()
		updatedProduct.getName() == otherProduct.getName()
    }

    void "test delete"() {
        setup:
        String id = client.toBlocking().retrieve(HttpRequest.POST('/store/product', sampleProduct))

        when:
        HttpStatus status = client.toBlocking().retrieve(HttpRequest.DELETE('/store/product/' + id), Argument.of(HttpStatus).type)
        Product productReturned = client.toBlocking().retrieve(HttpRequest.GET('/store/product/' + id), Argument.of(Product).type)

        then:
        status == OK
        thrown HttpClientResponseException
		productReturned == null
    }

}