package gl51.project.store

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.http.client.RxHttpClient
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class MemoryProductStorageTest extends Specification {

    ProductStorage store = new MemoryProductStorage()
    Product myProduct = new Product(name: "myProduct")

 	    def "empty storage returns empty list"() {
        expect:
        store.all() == []
    }

     def "check if adding takes effect"() {
        setup:
        store.save(new Product(name: "myProduct"))

         when:
        def all = store.all()

         then:
        all.size() == 1
        all.first().name == "myProduct"
    }

     def "check if saving takes effect"() {
        setup:
        def id = store.save(myProduct)

         expect:
        myProduct.id != null
        myProduct.id == id
    }

     def "check if deleting takes effect"() {
        setup:
        def id = store.save(myProduct)

         when:
        store.delete(id)

         then:
        !store.all().contains(myProduct)
    }

     def "check if updating takes effect"() {
        setup:
        def id = store.save(myProduct)

         when:
        Product myUpdatedProduct = new Product(name: "myUpdatedProduct")
        println(id);
        store.update(id, myUpdatedProduct)

         then:
        myProduct != myUpdatedProduct
    }

     def "Throw a NotExistingProductException if we get the id which it doesn't exist"() {
        setup:
        def id = myProduct.id

         when:
        store.getByID(id)

         then:
        thrown NotExistingProductException
    }

     def "get ID if it exist"() {
        setup:
        def id = store.save(myProduct)

         when:
        def gettedProduct = store.getByID(id)

         then:
        myProduct == gettedProduct
    }

   
}
