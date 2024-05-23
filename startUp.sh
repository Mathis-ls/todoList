#!/bin/zsh

prompt_for_input() {
  local var_name=$1
  local prompt_message=$2

  read "?$prompt_message: " input_value
  export $var_name=$input_value
}

# Prompt the user for each environment variable
prompt_for_input "MYSQL_DATABASE" "Enter the MySQL database name"
prompt_for_input "MYSQL_USER" "Enter the MySQL username"
prompt_for_input "MYSQL_PASSWORD" "Enter the MySQL password"
prompt_for_input "MYSQL_ROOT_PASSWORD" "Enter the MySQL ROOT password"

# Print the variables to confirm they are set (optional)
echo "MYSQL_DATABASE=${MYSQL_DATABASE}"
echo "MYSQL_USER=${MYSQL_USER}"
echo "MYSQL_PASSWORD=${MYSQL_PASSWORD}"
echo "MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}"

docker run -d -p 3306:3306 --name todoItemsMySqlDB \
  -e MYSQL_USER=${MYSQL_USER} \
  -e MYSQL_PASSWORD=${MYSQL_PASSWORD} \
  -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} \
  -e MYSQL_DATABASE=${MYSQL_DATABASE} \
   mysql:8.4.0

sleep 10

mvn spring-boot:run