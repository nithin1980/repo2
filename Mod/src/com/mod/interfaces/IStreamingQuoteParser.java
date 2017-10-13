package com.mod.interfaces;

import java.nio.ByteBuffer;


public interface IStreamingQuoteParser {

	public StreamingQuote parse(ByteBuffer pktBuffer, String time);
}
