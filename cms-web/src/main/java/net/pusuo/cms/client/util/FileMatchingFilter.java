/*
 * Copyright (C) Peter Sorotokin, 1997-1998
 *
 * Author grants you a non-exclusive, royalty free,
 * license to use, modify and redistribute this software in source
 * and binary code form, provided that this copyright notice and
 * license appear on all copies of the derived software source code.
 *
 * This software is provided "AS IS", without a warranty of any kind.
 * Use at your own risk.
 *
 */
package net.pusuo.cms.client.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 *  Class FileMatchingFilter provides an implementation for
 *  FilenameFilter, that emulates some of the UNIX csh filename
 *  expansion features. FileMatchingFilter (as any other FilenameFilter)
 *  works only withing a single directory. For pattern syntax
 *  and limitations of this implementation see
 *  description of FileMatchingPattern. Use FileMatchingPattern
 *  instead of FileMatchingFilter if you want to use patterns
 *  like n?t/S*.java (that go into subfolders).
 */
public class FileMatchingFilter implements FilenameFilter
{

  /**
   *  Characters that represent this pattern
   */
  char[] patt;

  /**
   *  Length of array patt
   */
  private int    pl;

  /**
   *  Constructs new FileMatchingFilter from its string representation
   */
  public FileMatchingFilter( String pattern )
  {
    patt = pattern.toCharArray();
    pl = patt.length;
  }

  /**
   *  this method determines if string candidate matches this pattern.
   *  parameter dir is not used and can be null.
   */
  public boolean accept( File dir, String candidate )
  {
    char[] arr = candidate.toCharArray();
    int a = 0;
    int p = 0;
    return match( arr, a, p );
  }

  /**
   *  Method that does all work. It calls itself recursively to
   *  implement pattern matching with rudimentary backtracking.
   *  @param arr representation of the filename we are currently matching
   *  @param a current offset in filename
   *  @param p current offset in the pattern
   */
  public boolean match( char[] arr, int a, int p )
  {

//    System.out.println( "Matching (" + new String( arr, a, arr.length-a ) +
//	 ") & (" +  new String( patt, p, patt.length-p ) + ")" );

    while( true )
      {
	if( p >= pl )
	  return a == arr.length;
	char pc = patt[p++];
	switch( pc )
	  {
	    case '[' :
	      if( a >= arr.length )
		return false;
	      else
	        {
		  char ac = arr[a++];
		  int sc = -1;
		  int pot_sc = -1;
		  boolean first = true;
		  boolean negate = false;
		  while( true )
		    {
		      if( p >= pl )
			return false;
		      pc = patt[p++];
		      // System.out.println( "--> [" + pc + "] [" + ac + "]" );
		      if( pc == '^' && first && !negate )
			{
			  negate = true;
			  continue;
			}
		      if( pc == ']' && ! first )
			if( negate )
			  {
			    p--;
			    break;
			  }
		        else
			  return false;
		      if( pc == '-' && pot_sc > 0 )
			{
			  sc = pot_sc;
			  continue;
			}
		      if( sc < 0 ? pc == ac : sc <= ac && ac <= pc )
			if( negate )
			  return false;
		        else
			  break;
		      first = false;
		      if( sc >= 0 )
			{
			  pot_sc = -1;
			  sc = -1;
			}
		      else
		        pot_sc = pc;
		    }
		  if( first )
		    p++;
		  for( ; p < pl && patt[p] != ']' ; p++ )
		    ;
		  p++;
	        }
	      break;
	    case '*' :
	      do
		{
		  if( match( arr, a++, p ) )
		    return true;
		}
	      while( a <= arr.length );
	      return false;
	    case '?' :
	      a++;
	      break;
	    default:
	      if( a >= arr.length || arr[a] != pc )
		return false;
	      a++;
	      break;
	  }
      }
  }

}
