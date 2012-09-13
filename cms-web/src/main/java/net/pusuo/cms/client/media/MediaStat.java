package net.pusuo.cms.client.media;

import java.util.*;
import java.sql.*;
import java.text.*;
import com.hexun.cms.client.db.Cms4Db;

public class MediaStat
{
	public static void main(String[] args)
	{
		MediaStat main = new MediaStat();

		
		main.execute();

		//main.resume("2005-7-14", "2005-7-16");
	}

	// ÿ���㳽���ǰһ������
	private void execute()
	{
		Calendar cal = new GregorianCalendar();
		cal.add( Calendar.DATE, -1 );
		String loaddate = new Timestamp(cal.getTimeInMillis()).toString().substring(0,10);

		stat( loaddate );
	}
	// ���ڴ���ָ�
	private void resume( String begindate, String enddate )
	{
		Calendar begincal = new GregorianCalendar();
		String[] begindates = begindate.split("-");
		begincal.set( Calendar.YEAR, Integer.parseInt(begindates[0]) );
		begincal.set( Calendar.MONTH, Integer.parseInt(begindates[1])-1 );
		begincal.set( Calendar.DATE, Integer.parseInt(begindates[2]) );
		begincal.set( Calendar.HOUR_OF_DAY, 0 );
		begincal.set( Calendar.MINUTE, 0 );
		begincal.set( Calendar.SECOND, 0 );
		begincal.set( Calendar.MILLISECOND, 0 );

		Calendar endcal = new GregorianCalendar();
		String[] enddates = enddate.split("-");
		endcal.set( Calendar.YEAR, Integer.parseInt(enddates[0]) );
		endcal.set( Calendar.MONTH, Integer.parseInt(enddates[1])-1 );
		endcal.set( Calendar.DATE, Integer.parseInt(enddates[2]) );
		endcal.set( Calendar.HOUR_OF_DAY, 0 );
		endcal.set( Calendar.MINUTE, 0 );
		endcal.set( Calendar.SECOND, 0 );
		endcal.set( Calendar.MILLISECOND, 0 );

		while( true )
		{
			if( begincal.getTimeInMillis()>endcal.getTimeInMillis() ) break;
			String loaddate = new Timestamp( begincal.getTimeInMillis() ).toString().substring(0,10);

			stat( loaddate );

			begincal.add( Calendar.DATE, 1 );
		}
	}

	private static long ct()
	{
		return System.currentTimeMillis();
	}
	private void stat( String loaddate )
	{
		int valid = -1;
		Connection c = null;
		CallableStatement cs = null;

		try
		{
			long t1 = ct();
			c = Cms4Db.getInstance().getConnection();
			cs = c.prepareCall("{?=call f_mediaused_news(?)}");
			cs.registerOutParameter(1,Types.INTEGER);
			cs.setString(2,loaddate);
			cs.execute();
			valid = cs.getInt(1);

			if( valid==0 )
			{
				System.out.println("gather "+loaddate+" success");
			} else {
				System.out.println("gather "+loaddate+" failure");
			}
			long t2 = ct();

			System.out.println("f_mediaused_news for "+loaddate+" "+(t2-t1));
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try
			{
				if( cs!=null ) cs.close();
				if( c!=null ) c.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	
}

