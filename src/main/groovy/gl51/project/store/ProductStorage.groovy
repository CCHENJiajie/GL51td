package gl51.project.store

interface ProductStorage {
	
	/**
     * creates an new product in the store and return id
     * @param p the product to store
     */
    String save(Product p)
	

    /**
     * updates an existing product in the store
     * Beware the product id must be filled in
     * @param p the product to update
     */
    void update(String id, Product p)

    /**
     * get a product by its id
     * @param id
     * @return a product
     */
    Product getByID(String id)

    /**
     * deletes a product by its id
     * @param id
     */
    void delete(String id)

    /**
     * list all products
     * @return a list of products
     */
    List<Product> all()
	
}
