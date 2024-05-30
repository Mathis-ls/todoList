#!/bin/zsh

# Function to prompt for user input
prompt_for_input() {
  local prompt_message=$1
  local input_value

  read "?$prompt_message: " input_value
  echo $input_value
}

# Function to prompt for user input for passwords
prompt_for_password() {
  local prompt_message=$1
  local input_value

  read -s "?$prompt_message: " input_value
  echo $input_value
}

# Function to update the .env file
update_env_file() {
    local env_file=".env"
    
    # Prompt for user inputs and store in variables
    local MYSQL_DATABASE=$(prompt_for_input "Enter MySQL database name")
    local MYSQL_USER=$(prompt_for_input "Enter MySQL user")
    local MYSQL_PASSWORD=$(prompt_for_password "Enter MySQL password")
    echo
    local MYSQL_ROOT_PASSWORD=$(prompt_for_password "Enter MySQL root password")
    echo
    local MYSQL_PORT=$(prompt_for_input "Enter MySQL port")

    # Write the values to the .env file
    echo "MYSQL_DATABASE=$MYSQL_DATABASE" > $env_file
    echo "MYSQL_USER=$MYSQL_USER" >> $env_file

    echo "MYSQL_PASSWORD=$MYSQL_PASSWORD" >> $env_file   
    echo "MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD" >> $env_file
   
    echo "MYSQL_PORT=$MYSQL_PORT" >> $env_file
    
    echo ".env file updated successfully."
}

update_env_file
