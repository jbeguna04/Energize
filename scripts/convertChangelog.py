import markdown2
text = open( '../README.md' ).read()
buf = markdown2.markdown( text )
test = open( 'test.html', 'w+' )
test.write( buf )
test.close()
