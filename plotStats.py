import sqlite3
from pylab import *

if __name__ == '__main__':
	xUncharging = []
	yUncharging = []
	xChargingUSB = []
	yChargingUSB = []
	xChargingAC = []
	yChargingAC = []
	
	#
	conn = sqlite3.connect( 'batteryStats.db' )
	c = conn.cursor()
	for row in c.execute( 'SELECT eventTime, chargingLevel, chargingState FROM rawBatteryStats ORDER BY eventTime;' ):
		if int( row[ 2 ] ) == 0:
			xUncharging.append( int( row[ 0 ] ) )
			yUncharging.append( int( row[ 1 ] ) )
		elif int( row[ 2 ] ) == 1:
			xChargingAC.append( int( row[ 0 ] ) )
			yChargingAC.append( int( row[ 1 ] ) )
		elif int( row[ 2 ] ) == 2:
			xChargingUSB.append( int( row[ 0 ] ) )
			yChargingUSB.append( int( row[ 1 ] ) )
	c.close()
	conn.commit()

	#
	l = plot( xUncharging, yUncharging, 'r.', xChargingAC, yChargingAC, 'g.', xChargingUSB, yChargingUSB, 'm.' )
	grid( True )
	title( 'Battery statistics' )
	xlabel( 'Unix timestamp' )
	ylabel( 'Charging percentage' )
	legend( (l), ( 'Decharging', 'Charging (AC)', 'Charging (USB)' ) )
	show()
