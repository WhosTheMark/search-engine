sudo -u postgres psql -f create_user.sql 
sudo -u postgres psql -f create_database.sql
export PGPASSWORD=search-engine
psql -d ridb -U searchengine -f create_table.sql

