# This is a basic VCL configuration file for varnish.  See the vcl(7)
# man page for details on VCL syntax and semantics.
# 
# Default backend definition.  Set this to point to your content
# server.
# 
 backend default {
     .host = "127.0.0.1";
     .port = "8080";
 }

# Restrict purge requests to local requests only.
 acl purge {
     "127.0.0.1";
 }
# 
# Below is a commented-out copy of the default VCL logic.  If you
# redefine any of these subroutines, the built-in logic will be
# appended to your code.

 sub vcl_recv {
	if (req.request == "PURGE") {
		if(!client.ip ~ purge) {
			error 405 "Not allowed.";
		}
		else {
			if(req.url ~ "search") {
				ban( "req.url ~ search" );
			}
			else if(req.url ~ "views") {
				ban("req.url ~ bills");
				ban("req.url ~ resolutions");
				ban("req.url ~ calendars");
				ban("req.url ~ meetings");
				ban("req.url ~ actions");
				ban("req.url ~ votes");
				ban("req.url ~ transcripts");
				ban("req.url ~ sponsor");
				ban("req.url ~ committee");
			}
			else if(req.url ~ "doc:") {
				set req.url = regsub(req.url, "/?doc:", "");
				ban("req.url ~ req.url");
			}
			else {
				ban_url(req.url);
			}
			set req.http.foobar = "Purged "+req.url;
			error 200 req.http.foobar;
			remove req.http.foobar;
		}
	}
	else {
		if(req.url ~ "^/legislation/?$") {
			return (pass);

		} else if(req.url ~ "^/legislation") {
			return (lookup);

		} else {
			return (pass);
		}
	}
 }
 
 sub vcl_pass {
     return (pass);
 }
 
 sub vcl_hash {
     hash_data(req.url);
     #set req.hash += req.url;
     if (req.http.host) {
	 hash_data(req.http.host);
         #set req.hash += req.http.host;
     } else {
         hash_data(server.ip);
         #set req.hash += server.ip;
     }
     return (hash);
 }
 
 sub vcl_hit {
     if (obj.ttl <= 0s) {
         return (pass);
     }
     return (deliver);
 }
 
 sub vcl_miss {
     return (fetch);
 }
 
 sub vcl_fetch {
     set beresp.ttl = 24h;
     return (deliver);
 }
 
 sub vcl_deliver {
     return (deliver);
 }
 
