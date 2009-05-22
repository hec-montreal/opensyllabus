FIXME:
The Hibernate (.xml) files should not be in the API project but in the Impl project (next to the implementation classes).
However, the current dependency from the API package toward GWT-shared makes so 
that with the way classloading is done in Sakai, this would require a specific HBM shared library
for Osyl. The clean way to solve this is to remove the GWT dependency from the API and move this dependency on the client side only.
