#!/bin/zsh

# Function to prompt for user input
prompt_for_input() {
  local var_name=$1
  local prompt_message=$2

  read "?$prompt_message: " input_value
  export $var_name=$input_value
}

# Function to prompt for user input for passwords
prompt_for_password() {
  local var_name=$1
  local prompt_message=$2

  read -s "?$prompt_message: " input_value
  echo
  export $var_name=$input_value
}

# Function to update the .env file
update_env_file() {
    local env_file=".env"
    
    # Prompt for user inputs
    prompt_for_input "MYSQL_DATABASE" "Enter MySQL database name"
    prompt_for_input "MYSQL_USER" "Enter MySQL user"
    prompt_for_password "MYSQL_PASSWORD" "Enter MySQL password"
    prompt_for_password "MYSQL_ROOT_PASSWORD" "Enter MySQL root password"
    prompt_for_input "MYSQL_PORT" "Enter MySQL port"

    # Write the values to the .env file
    echo "MYSQL_DATABASE=$MYSQL_DATABASE" > $env_file
    echo "MYSQL_USER=$MYSQL_USER" >> $env_file
    echo "MYSQL_PASSWORD=$MYSQL_PASSWORD" >> $env_file
    echo "MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD" >> $env_file
    echo "MYSQL_PORT=$MYSQL_PORT" >> $env_file
    
    echo ".env file updated successfully."
}

# Call the function to update the .env file
update_env_file
