**Money Transfer**
----

Simple API implementation for making money transfer between 2 accounts

**Create account**
----

* **URL**

   `/account`

 
* **Method:**
 
  `POST`
 
* **Request Body**

  ```json
   { 
    "name": "Bob",
   }
  ```
 
* **Response:**
  * **Code:** 201 <br />
    **Content:** 
    ```json
    {
        "id": "b37ab502-2657-4d73-b24c-43de11a9a181",
        "name": "Bob"
    }
    ```

**List transfers**
----

* **URL**

   `/account/:accountId/transfer`

 
* **Method:**
 
  `GET`

* **Path paramaters:**
  
  `accountId` - UUID, ex: `b37ab502-2657-4d73-b24c-43de11a9a181`
 
* **Response:**
  * **Code:** 200 <br />
    **Content:** 
    ```json
    {
        "accountId": "b37ab502-2657-4d73-b24c-43de11a9a181",
        "balance": 100,
        "transfers": [{
            "id": "df381172-0dd7-43d2-a3b6-ce7b87baf12b",
            "from": "93343028-c16d-4532-8f39-bef726185eed",
            "to": "b37ab502-2657-4d73-b24c-43de11a9a181",
            "amount": 100
        }]
    }
    ```

**Create transfer**
----

* **URL**

   `/transfer`

 
* **Method:**
 
  `POST`

* **Request Body**

  ```json
  { 
     "from": "b37ab502-2657-4d73-b24c-43de11a9a181",
     "to": "df381172-0dd7-43d2-a3b6-ce7b87baf12b",
     "amount": 100
  }
  ```
  
  Field `from` can be set to null to set account's initial balance.

* **Response:**
  * **Code:** 201 <br />
    **Content:** 
    ```json
    {
        "id": "93343028-c16d-4532-8f39-bef726185eed",
        "to": "df381172-0dd7-43d2-a3b6-ce7b87baf12b",
        "from": "b37ab502-2657-4d73-b24c-43de11a9a181",
        "amount": 100
    }
    ```
  * **Code:** 409. Is thrown to prevent race conditions during transfer. User should be able to resubmit form <br />
    **Content:** -
  * **Code:** 422. Account's balance is to little to perform transfer <br />
    **Content:** -



