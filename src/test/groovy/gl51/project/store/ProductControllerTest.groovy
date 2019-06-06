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

    def newProduct = new Product(name: "One")

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
        Product oldProduct = new Product(name: name, description: description, price: price, idealTemperature: idealTemperature)
        Product newProduct = new Product(name: name1, description: description1, price: price1, idealTemperature: idealTemperature1)
        String id = client.toBlocking().retrieve(HttpRequest.POST('/products', oldProduct))

        when:
        client.toBlocking().retrieve(HttpRequest.PUT('/products/' + id, newProduct), Argument.of(HttpStatus).type)
        Product updatedProduct = client.toBlocking().retrieve(HttpRequest.GET('/products/' + id), Argument.of(Product).type)

        then:
        updatedProduct.description == newProduct.description
        updatedProduct.price == newProduct.price
        updatedProduct.name == newProduct.name
        updatedProduct.idealTemperature == newProduct.idealTemperature

        where:
        name  | description | price | idealTemperature | name1 | description1 | price1  | idealTemperature1
        "aaa" | "bbb"       | 0.0   | 123000           | "ccc" | "ddd"        | 56465.3 | 64351568
    }

    void "test delete"() {
        setup:
        Product newProduct = new Product(name: name, description: description, price: price, idealTemperature: idealTemperature)
        String id = client.toBlocking().retrieve(HttpRequest.POST('/products', newProduct))

        when:
        client.toBlocking().retrieve(HttpRequest.DELETE('/products/'+ id))
        Product product = client.toBlocking().retrieve(HttpRequest.GET('/product/' + id), Argument.of(Product).type)

        then:
        thrown HttpClientResponseException

        where:
        name  | description | price | idealTemperature
        "aaa" | "bbb"       | 0.0   | 123000
    }

}