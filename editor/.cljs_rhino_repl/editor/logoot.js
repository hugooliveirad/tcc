// Compiled by ClojureScript 1.8.51 {}
goog.provide('editor.logoot');
goog.require('cljs.core');
goog.require('clojure.string');
editor.logoot.MAX_INT = (32767);
/**
 * Compare two pids. If all intersecting pos identifier are equal, the bigger
 *   pos vector will win.
 */
editor.logoot.compare_pid = (function editor$logoot$compare_pid(p__18338,p__18339){
var vec__18342 = p__18338;
var pos1 = cljs.core.nth.call(null,vec__18342,(0),null);
var vec__18343 = p__18339;
var pos2 = cljs.core.nth.call(null,vec__18343,(0),null);
if(cljs.core._EQ_.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [pos1], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [pos2], null))){
return (0);
} else {
var temp__4655__auto__ = cljs.core.first.call(null,cljs.core.filter.call(null,((function (vec__18342,pos1,vec__18343,pos2){
return (function (p1__18337_SHARP_){
return cljs.core.not_EQ_.call(null,(0),p1__18337_SHARP_);
});})(vec__18342,pos1,vec__18343,pos2))
,cljs.core.map.call(null,cljs.core.compare,pos1,pos2)));
if(cljs.core.truth_(temp__4655__auto__)){
var result = temp__4655__auto__;
return result;
} else {
if((cljs.core.count.call(null,pos1) > cljs.core.count.call(null,pos2))){
return (1);
} else {
return (-1);
}
}
}
});
editor.logoot.document = cljs.core.sorted_map_by.call(null,editor.logoot.compare_pid,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(0),(0)], null)], null),null], null),new cljs.core.Keyword(null,"lb","lb",-690221037),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(2),(1)], null)], null),(0)], null),"This is an example of a Logoot document",new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(1),(1)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(2),(2)], null)], null),(0)], null),"This is a line inserted between [1 1] and [2 2]",new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [editor.logoot.MAX_INT,(0)], null)], null),null], null),new cljs.core.Keyword(null,"le","le",-219152293));
/**
 * Creates an empty logoot document, with its beggining and finish lines
 */
editor.logoot.create_doc = (function editor$logoot$create_doc(){
return cljs.core.sorted_map_by.call(null,editor.logoot.compare_pid,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(0),(0)], null)], null),null], null),new cljs.core.Keyword(null,"lb","lb",-690221037),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [editor.logoot.MAX_INT,(0)], null)], null),null], null),new cljs.core.Keyword(null,"le","le",-219152293));
});
/**
 * Creates a list of grouped elements by index
 */
editor.logoot.zip = (function editor$logoot$zip(var_args){
var args__18123__auto__ = [];
var len__18116__auto___18345 = arguments.length;
var i__18117__auto___18346 = (0);
while(true){
if((i__18117__auto___18346 < len__18116__auto___18345)){
args__18123__auto__.push((arguments[i__18117__auto___18346]));

var G__18347 = (i__18117__auto___18346 + (1));
i__18117__auto___18346 = G__18347;
continue;
} else {
}
break;
}

var argseq__18124__auto__ = ((((0) < args__18123__auto__.length))?(new cljs.core.IndexedSeq(args__18123__auto__.slice((0)),(0),null)):null);
return editor.logoot.zip.cljs$core$IFn$_invoke$arity$variadic(argseq__18124__auto__);
});

editor.logoot.zip.cljs$core$IFn$_invoke$arity$variadic = (function (colls){
return cljs.core.apply.call(null,cljs.core.map,cljs.core.list,colls);
});

editor.logoot.zip.cljs$lang$maxFixedArity = (0);

editor.logoot.zip.cljs$lang$applyTo = (function (seq18344){
return editor.logoot.zip.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq18344));
});
/**
 * Returns a random integer between x (exclusive) and y (exclusive). Nil
 *   if there isn't integer between two numbers
 */
editor.logoot.rand_int_bet = (function editor$logoot$rand_int_bet(x,y){
if((Math.abs((x - y)) <= (1))){
return null;
} else {
if((x > y)){
return (((1) + y) + cljs.core.rand_int.call(null,((x - y) - (1))));
} else {
if((x < y)){
return (((1) + x) + cljs.core.rand_int.call(null,((y - x) - (1))));
} else {
return null;
}
}
}
});
/**
 * Generates a random position with lines between two numbers
 */
editor.logoot.rand_pos_bet = (function editor$logoot$rand_pos_bet(site,l1,l2){
var temp__4655__auto__ = editor.logoot.rand_int_bet.call(null,l1,l2);
if(cljs.core.truth_(temp__4655__auto__)){
var rand_line = temp__4655__auto__;
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [rand_line,site], null)], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [l1,site], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [editor.logoot.rand_int_bet.call(null,(0),editor.logoot.MAX_INT),site], null)], null);
}
});
/**
 * Generate a position between two positions
 */
editor.logoot.gen_pos = (function editor$logoot$gen_pos(site,pos1,pos2){
var p1 = cljs.core.first.call(null,pos1);
var p2 = cljs.core.first.call(null,pos2);
var pos1_rest = pos1;
var pos2_rest = pos2;
var pos_acc = cljs.core.PersistentVector.EMPTY;
while(true){
if((cljs.core.empty_QMARK_.call(null,pos1_rest)) || (cljs.core.empty_QMARK_.call(null,pos2_rest))){
if(!((p1 == null))){
return cljs.core.into.call(null,pos_acc,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1], null),editor.logoot.rand_pos_bet.call(null,site,cljs.core.first.call(null,p1),editor.logoot.MAX_INT)));
} else {
if(cljs.core.empty_QMARK_.call(null,pos2_rest)){
return cljs.core.into.call(null,pos_acc,editor.logoot.rand_pos_bet.call(null,site,cljs.core.ffirst.call(null,pos_acc),editor.logoot.MAX_INT));
} else {
return cljs.core.into.call(null,pos_acc,cljs.core.concat.call(null,cljs.core.butlast.call(null,pos2_rest),editor.logoot.rand_pos_bet.call(null,site,cljs.core.ffirst.call(null,pos_acc),cljs.core.first.call(null,cljs.core.last.call(null,pos2_rest)))));
}
}
} else {
if((cljs.core.first.call(null,p1) < cljs.core.first.call(null,p2))){
if((site >= cljs.core.second.call(null,p1))){
if(cljs.core._EQ_.call(null,cljs.core.count.call(null,pos1_rest),cljs.core.count.call(null,pos2_rest))){
return cljs.core.into.call(null,pos_acc,editor.logoot.rand_pos_bet.call(null,site,cljs.core.first.call(null,p1),cljs.core.first.call(null,p2)));
} else {
return cljs.core.into.call(null,pos_acc,cljs.core.concat.call(null,cljs.core.butlast.call(null,pos1_rest),editor.logoot.rand_pos_bet.call(null,site,cljs.core.first.call(null,cljs.core.last.call(null,pos1_rest)),editor.logoot.MAX_INT)));
}
} else {
if(cljs.core._EQ_.call(null,cljs.core.count.call(null,pos1_rest),cljs.core.count.call(null,pos2_rest))){
return cljs.core.into.call(null,pos_acc,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1], null),editor.logoot.rand_pos_bet.call(null,site,cljs.core.first.call(null,p1),editor.logoot.MAX_INT)));
} else {
return cljs.core.into.call(null,pos_acc,cljs.core.concat.call(null,cljs.core.butlast.call(null,pos1_rest),editor.logoot.rand_pos_bet.call(null,site,cljs.core.first.call(null,cljs.core.last.call(null,pos1_rest)),editor.logoot.MAX_INT)));
}
}
} else {
var pos_acc__$1 = cljs.core.conj.call(null,pos_acc,p1);
var pos1_rest__$1 = cljs.core.rest.call(null,pos1_rest);
var pos2_rest__$1 = cljs.core.rest.call(null,pos2_rest);
var p1__$1 = cljs.core.first.call(null,pos1_rest__$1);
var p2__$1 = cljs.core.first.call(null,pos2_rest__$1);
var G__18348 = p1__$1;
var G__18349 = p2__$1;
var G__18350 = pos1_rest__$1;
var G__18351 = pos2_rest__$1;
var G__18352 = pos_acc__$1;
p1 = G__18348;
p2 = G__18349;
pos1_rest = G__18350;
pos2_rest = G__18351;
pos_acc = G__18352;
continue;

}
}
break;
}
});
/**
 * Returns the index of a given pid
 */
editor.logoot.pid__GT_index = (function editor$logoot$pid__GT_index(doc,pid){
return cljs.core.first.call(null,cljs.core.keep_indexed.call(null,(function (p1__18354_SHARP_,p2__18353_SHARP_){
if(cljs.core._EQ_.call(null,pid,p2__18353_SHARP_)){
return p1__18354_SHARP_;
} else {
return null;
}
}),cljs.core.keys.call(null,doc)));
});
/**
 * Returns the pid of a given index
 */
editor.logoot.index__GT_pid = (function editor$logoot$index__GT_pid(doc,index){
return cljs.core.nth.call(null,cljs.core.keys.call(null,doc),index,null);
});
/**
 * Inserts the content into pid key of the given document
 */
editor.logoot.insert = (function editor$logoot$insert(doc,pid,content){
return cljs.core.assoc.call(null,doc,pid,content);
});
/**
 * Inserts the content after a line index
 */
editor.logoot.insert_after = (function editor$logoot$insert_after(doc,site,clock,index,content){
var vec__18359 = editor.logoot.index__GT_pid.call(null,doc,index);
var pos1 = cljs.core.nth.call(null,vec__18359,(0),null);
var vec__18360 = editor.logoot.index__GT_pid.call(null,doc,(index + (1)));
var pos2 = cljs.core.nth.call(null,vec__18360,(0),null);
return ((function (vec__18359,pos1,vec__18360,pos2){
return (function (p1__18356_SHARP_){
return editor.logoot.insert.call(null,doc,p1__18356_SHARP_,content);
});})(vec__18359,pos1,vec__18360,pos2))
.call(null,((function (vec__18359,pos1,vec__18360,pos2){
return (function (p1__18355_SHARP_){
return cljs.core.conj.call(null,cljs.core.PersistentVector.EMPTY,p1__18355_SHARP_,clock);
});})(vec__18359,pos1,vec__18360,pos2))
.call(null,editor.logoot.gen_pos.call(null,site,pos1,pos2)));
});
/**
 * Removes pid key from the given document
 */
editor.logoot.delete$ = (function editor$logoot$delete(doc,pid){
return cljs.core.dissoc.call(null,doc,pid);
});
/**
 * Given a logoot document, transform each line to a hash-map
 */
editor.logoot.doc__GT_hash_map = (function editor$logoot$doc__GT_hash_map(doc){
return cljs.core.map.call(null,(function (p__18364){
var vec__18365 = p__18364;
var vec__18366 = cljs.core.nth.call(null,vec__18365,(0),null);
var pos = cljs.core.nth.call(null,vec__18366,(0),null);
var clock = cljs.core.nth.call(null,vec__18366,(1),null);
var content = cljs.core.nth.call(null,vec__18365,(1),null);
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"pos","pos",-864607220),pos,new cljs.core.Keyword(null,"clock","clock",-894301127),clock,new cljs.core.Keyword(null,"content","content",15833224),content], null);
}),doc);
});
/**
 * Given a position, returns a string representation of it
 */
editor.logoot.pos__GT_logoot_str = (function editor$logoot$pos__GT_logoot_str(pos){
return clojure.string.join.call(null,".",cljs.core.map.call(null,(function (x){
return [cljs.core.str("["),cljs.core.str(x),cljs.core.str("]")].join('');
}),cljs.core.map.call(null,cljs.core.partial.call(null,clojure.string.join,", "),pos)));
});
/**
 * Given a logoot document, returns a string representation of it
 */
editor.logoot.doc__GT_logoot_str = (function editor$logoot$doc__GT_logoot_str(doc){
return clojure.string.join.call(null,"\n",cljs.core.map.call(null,(function (line){
return [cljs.core.str("((("),cljs.core.str(new cljs.core.Keyword(null,"pos","pos",-864607220).cljs$core$IFn$_invoke$arity$1(line)),cljs.core.str("), "),cljs.core.str(new cljs.core.Keyword(null,"clock","clock",-894301127).cljs$core$IFn$_invoke$arity$1(line)),cljs.core.str("), "),cljs.core.str(new cljs.core.Keyword(null,"content","content",15833224).cljs$core$IFn$_invoke$arity$1(line)),cljs.core.str(")")].join('');
}),cljs.core.map.call(null,(function (line){
return cljs.core.assoc.call(null,line,new cljs.core.Keyword(null,"pos","pos",-864607220),editor.logoot.pos__GT_logoot_str.call(null,new cljs.core.Keyword(null,"pos","pos",-864607220).cljs$core$IFn$_invoke$arity$1(line)));
}),editor.logoot.doc__GT_hash_map.call(null,doc))));
});

//# sourceMappingURL=logoot.js.map