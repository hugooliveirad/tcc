// Compiled by ClojureScript 1.8.51 {}
goog.provide('cljs.repl');
goog.require('cljs.core');
cljs.repl.print_doc = (function cljs$repl$print_doc(m){
cljs.core.println.call(null,"-------------------------");

cljs.core.println.call(null,[cljs.core.str((function (){var temp__4657__auto__ = new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__4657__auto__)){
var ns = temp__4657__auto__;
return [cljs.core.str(ns),cljs.core.str("/")].join('');
} else {
return null;
}
})()),cljs.core.str(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Protocol");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m))){
var seq__18216_18230 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m));
var chunk__18217_18231 = null;
var count__18218_18232 = (0);
var i__18219_18233 = (0);
while(true){
if((i__18219_18233 < count__18218_18232)){
var f_18234 = cljs.core._nth.call(null,chunk__18217_18231,i__18219_18233);
cljs.core.println.call(null,"  ",f_18234);

var G__18235 = seq__18216_18230;
var G__18236 = chunk__18217_18231;
var G__18237 = count__18218_18232;
var G__18238 = (i__18219_18233 + (1));
seq__18216_18230 = G__18235;
chunk__18217_18231 = G__18236;
count__18218_18232 = G__18237;
i__18219_18233 = G__18238;
continue;
} else {
var temp__4657__auto___18239 = cljs.core.seq.call(null,seq__18216_18230);
if(temp__4657__auto___18239){
var seq__18216_18240__$1 = temp__4657__auto___18239;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__18216_18240__$1)){
var c__17857__auto___18241 = cljs.core.chunk_first.call(null,seq__18216_18240__$1);
var G__18242 = cljs.core.chunk_rest.call(null,seq__18216_18240__$1);
var G__18243 = c__17857__auto___18241;
var G__18244 = cljs.core.count.call(null,c__17857__auto___18241);
var G__18245 = (0);
seq__18216_18230 = G__18242;
chunk__18217_18231 = G__18243;
count__18218_18232 = G__18244;
i__18219_18233 = G__18245;
continue;
} else {
var f_18246 = cljs.core.first.call(null,seq__18216_18240__$1);
cljs.core.println.call(null,"  ",f_18246);

var G__18247 = cljs.core.next.call(null,seq__18216_18240__$1);
var G__18248 = null;
var G__18249 = (0);
var G__18250 = (0);
seq__18216_18230 = G__18247;
chunk__18217_18231 = G__18248;
count__18218_18232 = G__18249;
i__18219_18233 = G__18250;
continue;
}
} else {
}
}
break;
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m))){
var arglists_18251 = new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__17046__auto__ = new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__17046__auto__)){
return or__17046__auto__;
} else {
return new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m);
}
})())){
cljs.core.prn.call(null,arglists_18251);
} else {
cljs.core.prn.call(null,((cljs.core._EQ_.call(null,new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.first.call(null,arglists_18251)))?cljs.core.second.call(null,arglists_18251):arglists_18251));
}
} else {
}
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"special-form","special-form",-1326536374).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Special Form");

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.contains_QMARK_.call(null,m,new cljs.core.Keyword(null,"url","url",276297046))){
if(cljs.core.truth_(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))){
return cljs.core.println.call(null,[cljs.core.str("\n  Please see http://clojure.org/"),cljs.core.str(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))].join(''));
} else {
return null;
}
} else {
return cljs.core.println.call(null,[cljs.core.str("\n  Please see http://clojure.org/special_forms#"),cljs.core.str(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Macro");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"REPL Special Function");
} else {
}

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
var seq__18220 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"methods","methods",453930866).cljs$core$IFn$_invoke$arity$1(m));
var chunk__18221 = null;
var count__18222 = (0);
var i__18223 = (0);
while(true){
if((i__18223 < count__18222)){
var vec__18224 = cljs.core._nth.call(null,chunk__18221,i__18223);
var name = cljs.core.nth.call(null,vec__18224,(0),null);
var map__18225 = cljs.core.nth.call(null,vec__18224,(1),null);
var map__18225__$1 = ((((!((map__18225 == null)))?((((map__18225.cljs$lang$protocol_mask$partition0$ & (64))) || (map__18225.cljs$core$ISeq$))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18225):map__18225);
var doc = cljs.core.get.call(null,map__18225__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists = cljs.core.get.call(null,map__18225__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name);

cljs.core.println.call(null," ",arglists);

if(cljs.core.truth_(doc)){
cljs.core.println.call(null," ",doc);
} else {
}

var G__18252 = seq__18220;
var G__18253 = chunk__18221;
var G__18254 = count__18222;
var G__18255 = (i__18223 + (1));
seq__18220 = G__18252;
chunk__18221 = G__18253;
count__18222 = G__18254;
i__18223 = G__18255;
continue;
} else {
var temp__4657__auto__ = cljs.core.seq.call(null,seq__18220);
if(temp__4657__auto__){
var seq__18220__$1 = temp__4657__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__18220__$1)){
var c__17857__auto__ = cljs.core.chunk_first.call(null,seq__18220__$1);
var G__18256 = cljs.core.chunk_rest.call(null,seq__18220__$1);
var G__18257 = c__17857__auto__;
var G__18258 = cljs.core.count.call(null,c__17857__auto__);
var G__18259 = (0);
seq__18220 = G__18256;
chunk__18221 = G__18257;
count__18222 = G__18258;
i__18223 = G__18259;
continue;
} else {
var vec__18227 = cljs.core.first.call(null,seq__18220__$1);
var name = cljs.core.nth.call(null,vec__18227,(0),null);
var map__18228 = cljs.core.nth.call(null,vec__18227,(1),null);
var map__18228__$1 = ((((!((map__18228 == null)))?((((map__18228.cljs$lang$protocol_mask$partition0$ & (64))) || (map__18228.cljs$core$ISeq$))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18228):map__18228);
var doc = cljs.core.get.call(null,map__18228__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists = cljs.core.get.call(null,map__18228__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name);

cljs.core.println.call(null," ",arglists);

if(cljs.core.truth_(doc)){
cljs.core.println.call(null," ",doc);
} else {
}

var G__18260 = cljs.core.next.call(null,seq__18220__$1);
var G__18261 = null;
var G__18262 = (0);
var G__18263 = (0);
seq__18220 = G__18260;
chunk__18221 = G__18261;
count__18222 = G__18262;
i__18223 = G__18263;
continue;
}
} else {
return null;
}
}
break;
}
} else {
return null;
}
}
});

//# sourceMappingURL=repl.js.map