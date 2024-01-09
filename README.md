# API Platform test

[![Build Status](https://travis-ci.org/hmrc/api-platform-test.svg)](https://travis-ci.org/hmrc/api-platform-test) [ ![Download](https://api.bintray.com/packages/hmrc/releases/api-platform-test/images/download.svg) ](https://bintray.com/hmrc/releases/api-platform-test/_latestVersion)

Dummy API created for testing purposes.
It defines all possible scenarios/combinations of resources/endpoints and segments.

---

### Examples of requests

##### resource: `/`
##### version: `2.0`
##### curl request:
```
curl -v -X GET "[ENVIRONMENT-DOMAIN]/api-platform-test/" -H 'Accept: application/vnd.hmrc.2.0+json' -H "Cache-Control: no-cache"
```
##### response code status: `200 OK`
##### response body:
```
{"uri":"/","method":"GET","resourceDetails":"Ciao Empty!"}
```

---

##### resource: `/ciao`
##### version: `2.0`
##### curl request:
```
curl -v -X GET "[ENVIRONMENT-DOMAIN]/api-platform-test/ciao?surname=Djokovic&firstName=Novak" -H 'Accept: application/vnd.hmrc.2.0+json' -H "Cache-Control: no-cache"
```
##### response code status: `200 OK`
##### response body:
```
{"uri":"/ciao?surname=Djokovic&firstName=Novak","method":"GET","resourceDetails":"Ciao: Some(Djokovic), Some(Novak), None"}
```

---

##### resource: `/ciao/:surname`
##### version: `2.0`
##### curl request:
```
curl -v -X GET "[ENVIRONMENT-DOMAIN]/api-platform-test/ciao/Tesla" -H 'Accept: application/vnd.hmrc.2.0+json' -H "Cache-Control: no-cache"
```
##### response code status: `200 OK`
##### response body:
```
{"uri":"/ciao/Tesla","method":"GET","resourceDetails":"Ciao Surname: Tesla"}
```

---

##### resource: `/ciao/:surname/:firstName`
##### version: `2.0`
##### curl request:
```
curl -v -X GET "[ENVIRONMENT-DOMAIN]/api-platform-test/ciao/Rossi/Valentino?middleName=Alvise"" -H 'Accept: application/vnd.hmrc.2.0+json' -H "Cache-Control: no-cache"
```
##### response code status: `200 OK`
##### response body:
```
{"uri":"/ciao/Rossi/Valentino?middleName=Alvise","method":"GET","resourceDetails":"Ciao Full Name: Rossi, Valentino, Some(Alvise)"}
```

---

##### resource: `/ciao/kennedy/john/fitzgerald`
##### version: `2.0`
##### curl request:
```
curl -v -X GET "[ENVIRONMENT-DOMAIN]/api-platform-test/ciao/kennedy/john/fitzgerald" -H 'Accept: application/vnd.hmrc.2.0+json' -H "Cache-Control: no-cache"
```
##### response code status: `404 Not Found`
##### response body:
```
{ "code" : "MATCHING_RESOURCE_NOT_FOUND", "message" : "A resource with the name in the request can not be found in the API" }
```

---
---

##### resource: `/city-details/:cityName/address`
##### version: `3.0`
##### curl request:
```
curl -v -X GET|PUT "[ENVIRONMENT-DOMAIN]/api-platform-test/details/Venice/address" -H 'Accept: application/vnd.hmrc.3.0+json' -H "Cache-Control: no-cache"
```
##### response code status: `200 OK`
##### response body:
```
{"uri":"/city-details/Venice/address","method":"GET|PUT","resourceDetails":"City: Venice, Address: Oxford Street"}
```

---

##### resource: `/city-details/:cityName/:postcode`
##### version: `3.0`
##### curl request:
```
curl -v -X GET|DELETE|POST "[ENVIRONMENT-DOMAIN]/api-platform-test/details/Venice/30016" -H 'Accept: application/vnd.hmrc.3.0+json' -H "Cache-Control: no-cache"
```
##### response code status: `200 OK`
##### response body:
```
{"uri":"/city-details/Venice/30016","method":"GET|DELETE|POST","resourceDetails":"City: Venice, Postcode: 30016"}
```

---

### Unit tests
```
sbt run-all-tests
```

---

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
