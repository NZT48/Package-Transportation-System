SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TRIGGER [dbo].[TR_TransportOffer] ON [dbo].[OfferToTransport] 
	FOR DELETE
AS 
BEGIN

   declare @PacketId int
   declare @kursor cursor

   set @kursor = cursor for
		select DISTINCT PacketId
		from deleted

		open @kursor

		fetch next from @kursor
		into @PacketId

		while @@FETCH_STATUS = 0
		begin

			delete from OfferToTransport
			where PacketId=@PacketId

			fetch next from @kursor
			into @PacketId
		end

		close @kursor
		deallocate @kursor

END
GO

ALTER TABLE [dbo].[OfferToTransport] ENABLE TRIGGER [TR_TransportOffer]
GO