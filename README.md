# Food Analyzer

## Introduction

Food Analyzer is a project which provides nutrition, ingredients and information about foods specified by the user.
Food Analyzer has two programs. The first one is a server which can handle multiple client connections
and provide the clients information about the requested foods. The second one is the the client program which is used to connect to the server, send requests about
the desired food and display the response from the server to the user.

## Supported commands

- `get-food <food_name>` - Returns information about the description and fdcId of all foods which match the given food name
- `get-food-report <food_fdcId>` - Returns the name, ingredients, calories, protein, fat, carbohydrates and fiber of a food with the given food id (`fdcId`)
- `get-food-by-barcode --code=<gtinUpc_code>|--img=<barcode_image_file>` - Returns information about a branded food by a gtinUPC code or by a path to an image of a gtinUPC code

### Example commands

```bash
get-food beef noodle soup
get-food-report 415269
get-food-by-barcode --code=009800146130
get-food-by-barcode --img=D:\Photos\BarcodeImage.jpg
get-food-by-barcode --img=D:\Photos\BarcodeImage.jpg --code=009800146130
```

## Project Implementation Description

### Food Analyzer Server

A Java NIO server which can handle multiple client connections. When a client makes a request to the server a new thread is created and passed
to a thread pool executor service in which the request will be executed and the response will be sent back to the client. Additional threads 
are being used for the requests since all data about the foods are being fetched from a food API and since it takes time to make a request to
that API all other clients will have to wait for the current request to finish which isn't what we want. And we use Java NIO instead of Java NET
since we need additional threads only when a call to the API is being made. To decrease the response time even more, the server has a cache
system implemented wich stores and retrieves information about requests which were already made. The cache has two layers. The first one 
stores the data in a file system and the second one stores it in the heap memory until a specific limit is reached to avoid memory overflowing.

### Food Analyzer Clinet

A Java NIO client which is used to connect to a server and send messages in the form of request and then output to the console the response from
the server.