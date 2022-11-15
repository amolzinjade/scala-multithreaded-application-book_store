import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket
import scala.io.StdIn

object TCP_Client extends App {
    def printServices() : Unit = {
        println(Console.BOLD + Console.UNDERLINED + "Services" + Console.RESET)
        println("1. Add New Book")
        println("2. Show All Books")
        println("3. View price of the book")
        println("4. Exit")
        print("Enter your choice: ")
    }

    var loopProgram : Boolean = true
    println(Console.CYAN + "TCP Book Search Program" + Console.RESET)
    
    while (loopProgram) {
        printServices
        try {

            val serviceCode = StdIn.readLine().toInt

            if (serviceCode == 4) {
                loopProgram = false
            }
            else if(serviceCode == 1) {
                println("Enter ISBN Number:")
                val isbnNumber = StdIn.readLine().toInt

                println("Enter Book Name:")
                val book_name = StdIn.readLine()

                println("Enter Price:")
                val price = StdIn.readLine().toFloat

                val clientSocket = new Socket("localhost", 2000)
                val inputStream = new DataInputStream(clientSocket.getInputStream())
                val outputStream = new DataOutputStream(clientSocket.getOutputStream())
                outputStream.writeBytes(serviceCode.toString + "\n")
                outputStream.writeBytes(isbnNumber.toString + "\n")
                outputStream.writeBytes(book_name + "\n")
                outputStream.writeBytes(price.toString + "\n")
                println("\n" + Console.BOLD + "Received respond from server:" + Console.RESET)
                println(inputStream.readLine().toString + "\n")
                clientSocket.close()
            }
            else if(serviceCode == 3) {
                println("Provide ISBN Number:")
                val isbnNumber = StdIn.readLine().toInt
                val clientSocket = new Socket("localhost", 2000)
                val inputStream = new DataInputStream(clientSocket.getInputStream())
                val outputStream = new DataOutputStream(clientSocket.getOutputStream())
                outputStream.writeBytes(serviceCode.toString + "\n")
                outputStream.writeBytes(isbnNumber.toString + "\n")
                println("\n" + Console.BOLD + "Received respond from server:" + Console.RESET)
                println(inputStream.readLine().toString + "\n")
                clientSocket.close()
                }
            else {
                val clientSocket = new Socket("localhost", 2000)
                val inputStream = new DataInputStream(clientSocket.getInputStream())
                val outputStream = new DataOutputStream(clientSocket.getOutputStream())
                outputStream.writeBytes(serviceCode.toString + "\n")
                println("\n" + Console.BOLD + "Received respond from server:" + Console.RESET)
                println(inputStream.readLine() + "\n")
                clientSocket.close()
            }
        } catch{
            case x : Throwable => {
                println(Console.RED + "Invalid Input, Try Again \n" + Console.RESET)
            }
        }
    }

}