# CSC460 Final Project
Contributers: Jake Bode, Francisco Gonzalez

## Contributions
Jake Bode focused on the normalization analysis and database schema design as well as the coding for the course and class operations.
Francisco Gonzalez completed much of the code organization and the programming of most of the operations.

## Prerequisites

* Have access to the Lectura server hosted at the University of Arizona
* Account created for the Oracle Database on Lectura  
* Add Oracle JDBC to classpath
* Java 16 installed  

<!-- end of list -->

**_If you encounter any issues contact the helpdesk through their website or email lab@cs.arizona.edu_**  

## Setup

1) Ensure [prerequisites](##Prerequisites) are complete

2) Compile all necessary java files by running the following command  
`javac Program4.java`

## Running the program
1) Complete [Prerequisites](#prerequisites) and [Setup](#setup)
2) Start the java program by running  
`java Program4 -u <oracle username> -p <oracle password>`
3) Text-based application will start allowing for interactions with the Fitness Database

## Development Notes

* If you want to cleanup the .class files that are generated run the following command from the <i>src</i> directory  
`make clean`
