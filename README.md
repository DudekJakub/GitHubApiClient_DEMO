# GitHubApiClient_Kohsuke_DEMO
This is a demo application that uses the `kohsuke` library to connect to the GitHub Rest API and retrieve data on the user's repositories.

<br>

The application uses an synchronous, blocking classic approach, where requests are processed by so-called Servlets (used default server: `Tomcat`).

<br>

The main assumption of the project is to provide an API that, using GitHub Rest API v.3 as a backing-api, enables obtaining processed information on the user's repositories.

<br>

This is a side solution to the functional requirements listed below.

![image](https://github.com/DudekJakub/GitHubApiClient_Kohsuke_DEMO/assets/90628819/1e64b04e-ec24-4649-8ffa-a6f525f39b0b)

<br>

Due to lack of time this ReadMe will be completed on the day 09.08.2023. 
<br>
For now please visit main solution here:
<br>
LINK: https://github.com/DudekJakub/GitHubApiClient_WebFlux_DEMO

<br>

# HOW TO USE:

In order to run the application, Java version 17 and build-tool gradle are required.
<br><br>
In addition, to authenticate an application using GitHub Rest API v.3, it is required to generate the so-called. JsonWebToken. Instruction below:

`STEP 1`
<br>
Enter your GitHub account and click on your avatar picture:
<br>
![image](https://github.com/DudekJakub/GitHubApiClient_WebFlux_DEMO/assets/90628819/89b03ce7-bde1-4dfb-9862-dd979fdebba0)

<br>

`STEP 2`
<br>
Pick `settings` option:
<br>
![image](https://github.com/DudekJakub/GitHubApiClient_WebFlux_DEMO/assets/90628819/b9ada165-8ea3-4948-86be-9d4d79e6c551)

<br>

`STEP 3`
<br>
On the left vertical list of options go to the very last one called `Developer Settings`:
<br>
![image](https://github.com/DudekJakub/GitHubApiClient_WebFlux_DEMO/assets/90628819/09a5b98f-fc3c-447a-9404-2e419946a20e)

<br><br>

`STEP 4`
<br>
Now click `Personal access tokens` option:
<br>
![image](https://github.com/DudekJakub/GitHubApiClient_WebFlux_DEMO/assets/90628819/d58eb11d-e20e-4673-a100-152e64f0481c)

<br>

`STEP 5`
<br>
Pick `Fine-grained tokens` option:
<br>
![image](https://github.com/DudekJakub/GitHubApiClient_WebFlux_DEMO/assets/90628819/eb7c2cd4-789e-4fdc-9b1e-7a2632754444)

<br>

`STEP 6`
<br>
Click `Generate new token` button:
<br>
![image](https://github.com/DudekJakub/GitHubApiClient_WebFlux_DEMO/assets/90628819/f7a38f21-6080-45d3-8273-aa2b9e5578bc)

<br>

`STEP 7`
<br>
Confirm your credentials.

<br>

`STEP 8`
<br>
Provide token name and expiration time then pick:
<br>
![image](https://github.com/DudekJakub/GitHubApiClient_WebFlux_DEMO/assets/90628819/19c92556-3272-498b-9e2e-98be9c13b583)
<br>
...from `Permissions` list pick `Repository permissions` and grant access to following options: `actions`,`code`,`metadata`.

<br>

`STEP 9`
<br>
Now when your token is generated, copy it and paste it into application's environmental variables (with the name of variable `GITHUB_JWT=`):
<br>
![image](https://github.com/DudekJakub/GitHubApiClient_WebFlux_DEMO/assets/90628819/58fdc23f-83a8-4bc2-abf3-1cd7127a3d13)
<br>
![image](https://github.com/DudekJakub/GitHubApiClient_WebFlux_DEMO/assets/90628819/e65ec081-47ba-4a84-a1d3-27157c05e445)
<br>
![image](https://github.com/DudekJakub/GitHubApiClient_WebFlux_DEMO/assets/90628819/efeb35b5-a018-48ec-950e-74089e877397)

<br><br>

To create Windows system env. variable please following this link: 
<br>
https://docs.oracle.com/en/database/oracle/machine-learning/oml4r/1.5.1/oread/creating-and-modifying-environment-variables-on-windows.html

<br><br>

### IMPORTANT!
In case of integration tests JWT token need to be provided to TestClass run options (as env. variable) or for particular test if the test is about run by itself.
