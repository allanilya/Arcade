  
Create Table mc_leaderboard (
id Int Primary Key, username Varchar(10) Not Null, score Int Not Null, Foreign Key (id) References user_balance(id)
);

Create Table fifa_leaderboard (
id Int Primary Key, username Varchar(10) Not Null, score Int Not Null, Foreign Key (id) References user_balance(id)
);

Create Table ra_leaderboard (
id Int Primary Key, username Varchar(10) Not Null, score Int Not Null, Foreign Key (id) References user_balance(id)
);
