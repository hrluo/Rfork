print("you have successfully connected to R kernel.")
c1<-rnorm(1);
print(c1)
c2<-rexp(1);
c3<-c1+c2;
print(c3)
fun1<-function(size=5){
	return(rnorm(5));
}
print(fun1(7))
fun2<-function(x){
	return(paste(x,"->YES!",sep=""));
}