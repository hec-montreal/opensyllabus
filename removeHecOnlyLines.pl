#!/usr/bin/perl

# This short Perl script is used to filter HEC Montreal's source code to remove
# unwanted lines for the community version. For more information see Jira:
# http://jira.hec.ca/browse/SAKAI-2723
#
# The script is called before build, typically in a Hudson job. Syntax to call:
# perl script 1st_file_to_filter 2nd_file_to_filter etc.
#
# Syntax to use in any code (html, xml, java, etc.):
# A line containing the following string marks the start of lines to remove:
# BEGIN HEC ONLY SAKAI-2723
# A line containing the following string marks the end of lines to remove:
# END HEC ONLY SAKAI-2723
# Lines containing these strings will also be removed.
# Lines containing this string are also removed:
# HEC ONLY SAKAI-2723
#
# Examples:
# some code to keep;
# single line of code to remove; // HEC ONLY SAKAI-2723
# other code to keep;
# // BEGIN HEC ONLY SAKAI-2723
# // This is needed at HEC because of bla bla bla
# if (condition) {
#     some java code;
# }
# // END HEC ONLY SAKAI-2723
#
# <!-- BEGIN HEC ONLY SAKAI-2723 -->
#    <some>
#       <html>
#         <code>
#         </code>
#       </html>
#    </some>
# <!-- END HEC ONLY SAKAI-2723 -->
#
#

# arguments are file names
my (@files) = @ARGV;

# Whether we are in a block of lines to remove
my ($in_block) = 0;

# Process each specified file
foreach $file (@files) {
  # Open the file
  open(fileDescriptor, $file) or die ("Cannot read file $file: $!");
  print "Filtering $file...\n";

  # We keep the new contents in this variable
  my $content = "";
  # Process each line
  while (<fileDescriptor>) {
	$in_block = 1 if /BEGIN HEC ONLY SAKAI\-2723/;
	$content .= $_ if (not $in_block and not /HEC ONLY SAKAI-2723/);
	$in_block = 0 if /END HEC ONLY SAKAI\-2723/;
  }
  close fileDescriptor or die;

  # reopen the file for writing
  open(fileDescriptor, ">$file") or die ("Cannot write to file $file: $!");
  print fileDescriptor $content;
  #print "Wrote " . length ($content) . " bytes to $file\n";
}
