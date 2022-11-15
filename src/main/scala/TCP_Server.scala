import java.io.{DataInputStream, DataOutputStream}
import java.net.{ServerSocket, Socket}
import java.sql._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn.readLine

object TCP_Server extends App {
    val url = "jdbc:mysql://localhost:3306/book_store"
    val driver = "com.mysql.jdbc.Driver"
    val username = "root"
    val password = ""
    def getPrice(isbnNumber:Int) = {
        // connect to the database named "mysql" on port 8889 of localhost
        var price = ""
        var bookname = ""
        var connection: Connection = null
        try {

            Class.forName(driver)
            connection = DriverManager.getConnection(url, username, password)
            val statement = connection.createStatement
            val rs = statement.executeQuery("SELECT * FROM book WHERE ISBN =" + isbnNumber)
            while (rs.next) {

                price = rs.getString("PRICE")
            }
        } catch {
            case e: Exception => e.printStackTrace
        }
        connection.close
        price;
    }
    def getBookname(isbnNumber:Int) = {
        // connect to the database named "mysql" on port 8889 of localhost
        var price = ""
        var bookname = ""
        var connection: Connection = null
        try {

            Class.forName(driver)
            connection = DriverManager.getConnection(url, username, password)
            val statement = connection.createStatement
            val rs = statement.executeQuery("SELECT * FROM book WHERE ISBN =" + isbnNumber)
            while (rs.next) {
                bookname = rs.getString("BOOK_NAME")

            }
        } catch {
            case e: Exception => e.printStackTrace
        }
        connection.close
        bookname;
    }
    def insertBook(isbnNumber:Int,bookName:String,price:Float) = {
        // connect to the database named "mysql" on port 8889 of localhost
        var connection: Connection = null
        try {

            Class.forName(driver)
            connection = DriverManager.getConnection(url, username, password)

            val insertSql = """
                              |insert into book(ISBN,BOOK_NAME,PRICE)
                              |values (?,?,?)""".stripMargin
            val preparedStmt: PreparedStatement = connection.prepareStatement(insertSql)
            preparedStmt.setInt(1, isbnNumber)
            preparedStmt.setString (2, bookName)
            preparedStmt.setFloat(3, price)
            preparedStmt.execute
            println("Record Inserted Successfully")
            preparedStmt.close()
        } catch {
            case e: Exception => e.printStackTrace
        }
        connection.close

    }
    def showAllBooks() = {
        // connect to the database named "mysql" on port 8889 of localhost
        var connection: Connection = null
        var tuple = ("","","")
        try {

            Class.forName(driver)
            connection = DriverManager.getConnection(url, username, password)
            val statement = connection.createStatement
            val rs = statement.executeQuery("SELECT * FROM book ")
            while (rs.next) {
                val isbnNumber = rs.getString("ISBN")
                val bookName = rs.getString("BOOK_NAME")
                val price = rs.getString("PRICE")
                tuple=(isbnNumber,bookName,price)
                tuple;
            }
        } catch {
            case e: Exception => e.printStackTrace
        }
        connection.close

    }

    def service(clientSocket : Socket) : Unit = {
        try {
            // Setting up Futures for next Client Socket
            val TCPClientSocket: Future[Socket] = Future {
                serverSocket.accept()
            }
            TCPClientSocket.foreach(socket => service(socket))

            // Setting up I/O Streams for message passing
            val inputStream = new DataInputStream(clientSocket.getInputStream())
            val outputStream = new DataOutputStream(clientSocket.getOutputStream())



            // Service Code received
            val serviceCode = inputStream.readLine().toInt
            println(Console.CYAN + s"Client Socket: $clientSocket \nService Code: $serviceCode" + Console.RESET)


            // Matching Service Code
            serviceCode match {
                // Insert New Record
                case 1 => {
                    val isbnNumber = inputStream.readLine().toInt
                    val book_name = inputStream.readLine()
                    val price = inputStream.readLine().toFloat
                    insertBook(isbnNumber,book_name,price)
                    outputStream.writeBytes("Record Inserted Successfully\n")
                }

                // Show all books
                case 2 => {
                    var connection: Connection = null
                    var tuple = ("","","")

                    Class.forName(driver)
                    connection = DriverManager.getConnection(url, username, password)
                    val statement = connection.createStatement
                    val rs = statement.executeQuery("SELECT * FROM book ")
                    while (rs.next) {
                        val isbnNumber = rs.getString("ISBN")
                        val bookName = rs.getString("BOOK_NAME")
                        val price = rs.getString("PRICE")
                        tuple = (isbnNumber,bookName,price)
                        outputStream.writeBytes("    "+tuple.toString())
                    }

                    }

                // Print price and bookname
                case 3 => {
                    //Get the Price for ISBN number
                    // ISBN Number received
                    val isbnNumber = inputStream.readLine().toInt
                    val price = getPrice(isbnNumber)
                    val book = getBookname(isbnNumber)
                    println(Console.CYAN + s"Client Socket: $clientSocket \nPrice: $price" + Console.RESET)
                    outputStream.writeBytes("Price: " + price + "    "+"Book Name: " +book+ "\n")

                }

                // Unknown
                case _ => {
                    outputStream.writeBytes(s"Unknown Service Code: $serviceCode \n")
                }
            }
            println(Console.CYAN + s"Service Completed \nSocket: $clientSocket Terminated" + Console.RESET)
            clientSocket.close
            print("\nHit 'Enter' to quit the server!\n")
        }
        catch {
            case e: Exception => e.printStackTrace
        }
    }

    // Create Server Socket
    val serverSocket = new ServerSocket(2000)

    // Setting Futures for Client Sockets
    val TCPClientSocket : Future[Socket] = Future{ serverSocket.accept() }
    TCPClientSocket.foreach( socket => service(socket) )
    print("Server is now online\nHit 'Enter' to quit the server!\n")
    readLine()

    // Shut down Server Socket
    serverSocket.close()


}