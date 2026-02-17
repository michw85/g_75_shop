Общепринятые правила именования эндпоинтов:

1. Не использовать глаголы, в том числе обозначающие CRUD-операции.
   Использовать существительные.

Bad examples:

http://api.example.com/v1/store/CreateItems/{item-id}❌
http://api.example.com/v1/store/getEmployees/{emp-id}❌
http://api.example.com/v1/store/update-prices/{price-id}❌
http://api.example.com/v1/store/deleteOrders/{order-id}❌

Good examples:

http://api.example.com/v1/store/items/{item-id}✅
http://api.example.com/v1/store/employees/{emp-id}✅
http://api.example.com/v1/store/prices/{price-id}✅
http://api.example.com/v1/store/orders/{order-id}✅



2. Не использовать единственное число, если мы не обращаемся к ресурсу-синглтону.
   Использовать множественное число.

Bad examples (Typical and Singleton resources):

http://api.example.com/v1/store/item/{item-id}❌
http://api.example.com/v1/store/employee/{emp-id}/address❌

Good examples (Typical and Singleton resources):

http://api.example.com/v1/store/items/{item-id}✅
http://api.example.com/v1/store/employees/{emp-id}/address✅



3. Не использовать camelCase, слитное написание слов и символ подчёркивания в именах ресурсов.
   Использовать дефис.

Bad examples:

http://api.example.com/v1/store/vendormanagement/{vendor-id}❌
http://api.example.com/v1/store/itemmanagement/{item-id}/producttype❌
http://api.example.com/v1/store/inventory_management❌

Good examples:

http://api.example.com/v1/store/vendor-management/{vendor-id}✅
http://api.example.com/v1/store/item-management/{item-id}/product-type✅
http://api.example.com/v1/store/inventory-management✅



4. Не использовать расширения имён файлов.
   Просто опускать их.

Bad examples:

http://api.example.com/v1/store/items.json❌
http://api.example.com/v1/store/products.xml❌

Good examples:

http://api.example.com/v1/store/items✅
http://api.example.com/v1/store/products✅



5. Не использовать подстроку для поиска, фильтрации, сортировки.
   Использовать параметры.

❌Wrong:

/getUsers/2/10

✔️Correct:

/users?page=2&perPage=10



6. Не использовать сокращённые и не несущие смысла имена параметров.
   Использовать осмысленные имена.

❌Wrong:

/users?q=John
/books?search=history

✔️Correct:

/users?name=John
/books?category=history



7. Не использовать разные форматы для ответа.
   Соблюдать единообразие.