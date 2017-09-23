args = commandArgs(trailingOnly=TRUE)
#This args is an array that takes arguments from the Java by function record.sendR.
print(commandArgs)
if (length(args)==0) {
  stop("Warning:sendR:No arguments are provided!", call.=FALSE)
} else if (length(args)==1) {
  # default output file
  return(args[2]);
}