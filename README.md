


Batch insert Author and Book
```
curl --location --request POST 'localhost:8080/batchAction' \
--header 'Content-Type: application/json' \
--data-raw '[
  {
    "actionName": "insert_author",
    "paramMap": {
      "name": "John"
    }
  },
  {
    "actionName": "insert_book",
    "paramMap": {
      "price": 200,
      "name": "Java"
    }
  },
  {
    "actionName": "insert_book",
    "paramMap": {
      "price": 300,
      "name": "javascript"
    }
  }
]'
```
