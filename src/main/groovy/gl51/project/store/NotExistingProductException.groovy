package gl51.project.store

class NotExistingProductException extends Exception {
    NotExistingProductException(){
        super("The product has not been found !")
    }

    NotExistingProductException(String message){
        super(message)
    }
}
