SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[spGRANT_COURIER_REQUEST]
	@username varchar(100),
	@retVal int output
AS
BEGIN

	SET NOCOUNT ON;

	declare @Reg varchar(100)
	declare @CheckIfExist varchar(100)

	select @Reg=RegNum from CourierRequest where Username = @username;

	select @CheckIfExist=RegNum from Courier where RegNum=@Reg

	if @CheckIfExist=@Reg
	begin
		set @retVal = 0
	end
	else
	begin
		insert into Courier (Username, RegNum, Profit, Status) values(@Username, @Reg, 0, 0);
		delete from CourierRequest where Username=@Username;
		set @retVal = 1
	end

	return @retVal
END
GO


