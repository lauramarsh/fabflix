# cs122b-spring18-team-69

## Task 1

### How did you use connection pooling?

* Autocomplete.java        -  Lines 52-67
* BrowseServlet.java       -  Lines 91-106
* CartServlet.java         -  Lines 61-76
* CheckoutServlet.java     -  Lines 57-72
* LoginServlet.java        -  Lines 57-72
* MovieServlet.java        -  Lines 53-68
* SearchBox.java           -  Lines 53-68
* SearchPage.java          -  Lines 55-70
* StarServlet.java         -  Lines 52-67
* addNewMovie.java         -  Lines 49-64
* addNewStar.java          -  Lines 49-64
* employeeLogin.java       -  Lines 39-54
* metadata.java            -  Lines 54-69
* mobileSearchServelt.java -  Lines 49-64

- Use of Connection Pooling for reading:
![alt text](https://raw.githubusercontent.com/UCI-Chenli-teaching/cs122b-spring18-team-69/metrics/UseOfConnectionPoolingForReading.png)

- Use of Connection Pooling for Writing:
![alt text](https://raw.githubusercontent.com/UCI-Chenli-teaching/cs122b-spring18-team-69/metrics/UseOfConnectionPooolingForWriting.png)

### How did you use Prepared Statements?

* Autocomplete.java        -  Lines 105-117
* SearchBox.java           -  Lines 139-151
* SearchPage.java          -  Lines 140-156

- Use of Prepared Statments search box:
![alt text](https://raw.githubusercontent.com/UCI-Chenli-teaching/cs122b-spring18-team-69/metrics/UseOfPreparedStmtSearchBox.png)

- Use of Prepared Statments search box:
![alt text](https://raw.githubusercontent.com/UCI-Chenli-teaching/cs122b-spring18-team-69/metrics/UseOfPreparedStmtSearchBox2.png)


## Task 2

### Address of AWS and Google instances
* AWS instance        - http://52.15.158.77:8080/project1/index.html
* GOOGLE instance     - http://35.196.101.6:80/project1/index.html

## Have you verified that they are accessible? Does Fablix site get opened both on Google’s 80 port and AWS’ 8080 port?
Yes.  Both are verified and accessible.

### Explain how connection pooling works with two backend SQL (in your code)?
In our code, connection pooling works by sending requests on our site to one of 2 different identical master/slave instances.  Within the context.xml file, resources that connect to the mysql databases on these different instances are defined.

### How read/write requests were routed?
Read requests are routed to either of the Master/Slave instances our site is running on.  Write requests are sent only to the Master database, which is defined in context.xml as "MasterDB"


## Task 3

### Have you uploaded the log files to Github? Where is it located?

### Have you uploaded the HTML file (with all sections including analysis, written up) to Github? Where is it located?

### Have you uploaded the script  to Github? Where is it located?

### Have you uploaded the WAR file and README  to Github? Where is it located?

