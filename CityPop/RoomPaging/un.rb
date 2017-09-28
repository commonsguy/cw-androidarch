require 'nokogiri'
require 'sqlite3'
require 'securerandom'

doc = File.open("UNdata_Export_20170916_181900862.xml") { |f| Nokogiri::XML(f) }
db=SQLite3::Database.new 'un.db'

db.execute "CREATE TABLE cities (id TEXT PRIMARY KEY, country TEXT, city TEXT, population INTEGER)"

doc.css('record').each do |rec|
  country=rec.xpath('field[@name="Country"]')[0].content
  city=rec.xpath('field[@name="City"]')[0].content
  value=rec.xpath('field[@name="Value"]')[0].content.to_i

  db.execute("INSERT INTO cities (ID, country, city, population) VALUES (?, ?, ?, ?)", SecureRandom.uuid, country, city, value)
end

db.close
